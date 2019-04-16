package com.eresearch.elsevier.sciencedirect.consumer.connector;

import com.codahale.metrics.Timer;
import com.eresearch.elsevier.sciencedirect.consumer.connector.communicator.Communicator;
import com.eresearch.elsevier.sciencedirect.consumer.connector.guard.NoResultsAvailableGuard;
import com.eresearch.elsevier.sciencedirect.consumer.connector.guard.UniqueEntriesGuard;
import com.eresearch.elsevier.sciencedirect.consumer.connector.pagination.ScienceDirectSearchPaginationResourcesCalculator;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewLink;
import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.metrics.entries.ConnectorLayerMetricEntry;
import com.eresearch.elsevier.sciencedirect.consumer.worker.WorkerDispatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Log4j
public class ScienceDirectSearchConnectorImpl implements ScienceDirectSearchConnector {

    private static final String API_KEY = "apikey";
    private static final String QUERY = "query";

    private static final Integer DEFAULT_RESOURCE_PAGE_COUNT = 25;
    private static final String START_QUERY_PARAM = "start";
    private static final String COUNT_QUERY_PARAM = "count";

    private static final String FIRST_LINK_REF_VALUE = "first";
    private static final String LAST_LINK_REF_VALUE = "last";

    @Value("${scopus.apikey.consumer}")
    private String apiKey;

    @Value("${science-direct.author-search-url}")
    private String scienceDirectAuthorSearchUrl;

    @Qualifier("elsevierObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScienceDirectSearchPaginationResourcesCalculator scienceDirectSearchPaginationResourcesCalculator;

    @Autowired
    private WorkerDispatcher workerDispatcher;

    @Autowired
    @Qualifier("basicCommunicator")
    private Communicator communicator;

    @Autowired
    private UniqueEntriesGuard uniqueEntriesGuard;

    @Autowired
    private ConnectorLayerMetricEntry connectorLayerMetricEntry;

    @Autowired
    private NoResultsAvailableGuard noResultsAvailableGuard;

    @Override
    public List<ScienceDirectConsumerResultsDto> searchScienceDirectExhaustive(String query) throws BusinessProcessingException {

        Timer.Context context = connectorLayerMetricEntry.getConnectorLayerTimer().time();
        try {

            List<ScienceDirectConsumerResultsDto> results = new ArrayList<>();

            URI uri = constructUri(query);

            ScienceDirectConsumerResultsDto firstResult = pullAndFetchInfo(uri);
            results.add(firstResult);

            //check if we have results for the provided query...
            if (noResultsAvailableGuard.test(firstResult)) return Collections.emptyList();

            List<URI> allResourcesToHit = calculateAllResources(firstResult, query);

            if (workerDispatcher.shouldDispatch(allResourcesToHit.size())) { //if we need to split the load then...

                results.addAll(workerDispatcher.performTask(allResourcesToHit, extractInfoWithProvidedResource()));

            } else {

                for (URI resourceToHit : allResourcesToHit) {
                    ScienceDirectConsumerResultsDto result = pullAndFetchInfo(resourceToHit);
                    results.add(result);
                }

            }

            uniqueEntriesGuard.apply(results);
            return results;

        } catch (BusinessProcessingException e) {

            log.error("ScienceDirectSearchConnectorImpl#searchScienceDirectExhaustive --- error occurred.", e);
            throw e;

        } catch (RestClientException | IOException e) {

            log.error("ScienceDirectSearchConnectorImpl#searchScienceDirectExhaustive --- error occurred.", e);
            throw new BusinessProcessingException(
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR,
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage(),
                    e);

        } catch (CompletionException e) {

            log.error("ScienceDirectSearchConnectorImpl#searchScienceDirectExhaustive --- error occurred.", e.getCause());
            throw new BusinessProcessingException(
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR,
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage(),
                    e.getCause());

        } finally {
            context.stop();
        }

    }

    private Function<URI, ScienceDirectConsumerResultsDto> extractInfoWithProvidedResource() {
        return resource -> {
            try {
                return pullAndFetchInfo(resource);
            } catch (IOException e) {
                throw new CompletionException(e);
            }
        };
    }

    private List<URI> calculateAllResources(ScienceDirectConsumerResultsDto firstResult, String query) throws BusinessProcessingException {

        try {
            // Note: if we do not have a last resource page, we get out of here!
            Optional<ScienceDirectSearchViewLink> firstResourcePage = extractFirstResourcePage(firstResult);
            if (!firstResourcePage.isPresent()) {
                return Collections.emptyList();
            }

            // Note: if we do not have a last resource page, we get out of here!
            Optional<ScienceDirectSearchViewLink> lastResourcePage = extractLastResourcePageLink(firstResult);
            if (!lastResourcePage.isPresent()) {
                return Collections.emptyList();
            }

            List<String> startPageQueryParamsOfAllResources
                    = scienceDirectSearchPaginationResourcesCalculator.calculateStartPageQueryParams(firstResourcePage.get().getHref(),
                    lastResourcePage.get().getHref(),
                    DEFAULT_RESOURCE_PAGE_COUNT);

            return startPageQueryParamsOfAllResources.stream()
                    .map(startPageQueryParam -> constructUri(query,
                            startPageQueryParam,
                            String.valueOf(DEFAULT_RESOURCE_PAGE_COUNT)))
                    .collect(Collectors.toList());

        } catch (BusinessProcessingException ex) {

            log.error("ScienceDirectSearchConnectorImpl#calculateAllResources --- error occurred.", ex);
            throw ex;

        }

    }

    private Optional<ScienceDirectSearchViewLink> extractLastResourcePageLink(ScienceDirectConsumerResultsDto firstResult) {
        return firstResult
                .getScienceDirectConsumerSearchViewDto()
                .getLinks()
                .stream()
                .filter(link -> link.getRef().contains(LAST_LINK_REF_VALUE))
                .findAny();
    }

    private Optional<ScienceDirectSearchViewLink> extractFirstResourcePage(ScienceDirectConsumerResultsDto firstResult) throws BusinessProcessingException {
        return firstResult
                .getScienceDirectConsumerSearchViewDto()
                .getLinks()
                .stream()
                .filter(link -> link.getRef().contains(FIRST_LINK_REF_VALUE))
                .findAny();
    }

    private ScienceDirectConsumerResultsDto pullAndFetchInfo(URI uri)
            throws IOException {

        String resultInString = communicator.communicateWithElsevier(uri);
        return objectMapper.readValue(resultInString, ScienceDirectConsumerResultsDto.class);
    }

    private URI constructUri(String query, String startQueryParam, String countQueryParam) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(scienceDirectAuthorSearchUrl);
        builder.queryParam(API_KEY, apiKey);
        builder.queryParam(QUERY, query);
        builder.queryParam(START_QUERY_PARAM, startQueryParam);
        builder.queryParam(COUNT_QUERY_PARAM, countQueryParam);

        return builder.build().toUri();
    }

    private URI constructUri(String query) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(scienceDirectAuthorSearchUrl);
        builder.queryParam(API_KEY, apiKey);
        builder.queryParam(QUERY, query);

        return builder.build().toUri();
    }
}

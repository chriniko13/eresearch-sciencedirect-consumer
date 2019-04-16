package com.eresearch.elsevier.sciencedirect.consumer.service;

import com.codahale.metrics.Timer;
import com.eresearch.elsevier.sciencedirect.consumer.application.configuration.JmsConfiguration;
import com.eresearch.elsevier.sciencedirect.consumer.connector.ScienceDirectSearchConnector;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.SciDirQueueResultDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.metrics.entries.ServiceLayerMetricEntry;
import com.eresearch.elsevier.sciencedirect.consumer.repository.ScienceDirectConsumerRepository;
import com.eresearch.elsevier.sciencedirect.consumer.service.query.QueryToSend;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Log4j
public class ElsevierScienceDirectConsumerServiceImpl implements ElsevierScienceDirectConsumerService {

    private static final String DASH = " ";

    @Value("${enable.persistence.results}")
    private String enablePersistenceForResults;

    @Autowired
    private Map<String, QueryToSend> queriesToSend;

    @Autowired
    private ScienceDirectSearchConnector scienceDirectSearchConnector;

    @Autowired
    private Clock clock;

    @Autowired
    private ScienceDirectConsumerRepository scienceDirectConsumerRepository;

    @Autowired
    private ServiceLayerMetricEntry serviceLayerMetricEntry;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    @Qualifier("elsevierObjectMapper")
    private ObjectMapper objectMapper;

    @Override
    public ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerOperation(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto) throws BusinessProcessingException {

        Timer.Context context = serviceLayerMetricEntry.getServiceLayerTimer().time();
        try {

            ElsevierScienceDirectConsumerResultsDto result = new ElsevierScienceDirectConsumerResultsDto();
            String authorName = constructAuthorName(elsevierScienceDirectConsumerDto);

            List<ScienceDirectConsumerResultsDto> scienceDirectConsumerResultsDtos = new ArrayList<>();
            for (Map.Entry<String, QueryToSend> entry : queriesToSend.entrySet()) {

                QueryToSend queryToSend = entry.getValue();

                if (!queryToSend.shouldSend()) {
                    continue;
                }

                String query = queryToSend.get(authorName);
                scienceDirectConsumerResultsDtos.addAll(scienceDirectSearchConnector.searchScienceDirectExhaustive(query));
            }

            result.setRequestedElsevierScienceDirectConsumerDto(elsevierScienceDirectConsumerDto);
            result.setResultsSize(scienceDirectConsumerResultsDtos.size());
            result.setResults(scienceDirectConsumerResultsDtos);
            result.setOperationResult(Boolean.TRUE);
            result.setProcessFinishedDate(Instant.now(clock));

            if (Boolean.valueOf(enablePersistenceForResults)) {
                scienceDirectConsumerRepository.save(elsevierScienceDirectConsumerDto, result);
            }

            return result;
        } catch (BusinessProcessingException ex) {

            log.error("ElsevierScienceDirectConsumerServiceImpl#elsevierScienceDirectConsumerOperation --- error occurred.", ex);
            throw ex;

        } finally {
            context.stop();
        }
    }

    @Override
    public void sciDirNonBlockConsumption(String transactionId,
                                          ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto) {

        SciDirQueueResultDto sciDirQueueResultDto = null;
        try {

            ElsevierScienceDirectConsumerResultsDto sciDirResults
                    = this.elsevierScienceDirectConsumerOperation(elsevierScienceDirectConsumerDto);

            sciDirQueueResultDto = new SciDirQueueResultDto(transactionId, null, sciDirResults);

        } catch (BusinessProcessingException e) {

            log.error("ElsevierScienceDirectConsumerServiceImpl#sciDirNonBlockConsumption --- error occurred.", e);

            ElsevierScienceDirectConsumerResultsDto sciDirResultsDto = new ElsevierScienceDirectConsumerResultsDto();
            sciDirResultsDto.setOperationResult(false);

            sciDirQueueResultDto = new SciDirQueueResultDto(transactionId, e.toString(), sciDirResultsDto);

        } finally {

            try {
                String resultForQueue = objectMapper.writeValueAsString(sciDirQueueResultDto);
                jmsTemplate.convertAndSend(JmsConfiguration.QUEUES.SCIDIR_RESULTS_QUEUE, resultForQueue);
            } catch (JsonProcessingException e) {
                //we can't do much things for the moment here...
                log.error("ElsevierScienceDirectConsumerServiceImpl#sciDirNonBlockConsumption --- error occurred.", e);
            }

        }
    }

    /*
        Note: authorName = firstname + initials + surname.
     */
    private String constructAuthorName(ElsevierScienceDirectConsumerDto elsevierScopusConsumerDto) {
        return elsevierScopusConsumerDto.getFirstname() +
                Optional
                        .ofNullable(elsevierScopusConsumerDto.getInitials())
                        .filter(initials -> !initials.isEmpty())
                        .map(initials -> DASH + initials + DASH)
                        .orElse(DASH) +
                elsevierScopusConsumerDto.getSurname();
    }

}

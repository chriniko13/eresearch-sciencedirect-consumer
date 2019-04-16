package com.eresearch.elsevier.sciencedirect.consumer.connector.pagination;

import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

/*
 * Example to understand the code below:


 * "link": [
      {
        "@_fa": "true",
        "@href": "http://api.elsevier.com/content/search/author?start=0&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22",
        "@ref": "self",
        "@type": "application/json"
      },
      {
        "@_fa": "true",
        "@href": "http://api.elsevier.com/content/search/author?start=0&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22",
        "@ref": "first",
        "@type": "application/json"
      },
      {
        "@_fa": "true",
        "@href": "http://api.elsevier.com/content/search/author?start=25&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22",
        "@ref": "next",
        "@type": "application/json"
      },
      {
        "@_fa": "true",
        "@href": "http://api.elsevier.com/content/search/author?start=162&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22",
        "@ref": "last",
        "@type": "application/json"
      }
    ]


    firstResourcePage (href from the entity) = http://api.elsevier.com/content/search/author?start=0&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22

 	lastResourcePage (href from the entity) = http://api.elsevier.com/content/search/author?start=162&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22
 *
 * So the below operation will generate all the intermediate resource pages.
 *
 */
@Component
@Log4j
public class ScienceDirectSearchPaginationResourcesCalculatorImpl implements ScienceDirectSearchPaginationResourcesCalculator {


    @Override
    public List<String> calculateStartPageQueryParams(String firstResourcePageExclusive,
                                                      String lastResourcePageInclusive,
                                                      Integer resourcePageCount) throws BusinessProcessingException {

        try {

            List<String> results = new ArrayList<>();

            Map<String, String> firstResourcePageQueryParams = extractQueryParamsFromResourcePage(
                    firstResourcePageExclusive);

            Map<String, String> lastResourcePagePageQueryParams = extractQueryParamsFromResourcePage(
                    lastResourcePageInclusive);

            String secondPageStartStr = String
                    .valueOf(Integer.parseInt(firstResourcePageQueryParams.get("start")) + resourcePageCount);
            String lastPageStartStr = lastResourcePagePageQueryParams.get("start");

            Integer secondPageStart = Integer.parseInt(secondPageStartStr);
            Integer lastPageStart = Integer.parseInt(lastPageStartStr);

            Integer walker = secondPageStart;
            while (walker < lastPageStart) {

                results.add(String.valueOf(walker));

                walker += resourcePageCount;
            }

            //add the last page start at the end because elsevier has buggy behaviour in pagination.
            results.add(String.valueOf(lastPageStart));

            return results; // this will not contain the first resource page.

        } catch (BusinessProcessingException ex) {

            log.error("ScienceDirectSearchPaginationResourcesCalculatorImpl#calculateStartPageQueryParams --- error occurred.", ex);
            throw ex;

        } catch (NumberFormatException ex) {

            log.error("ScienceDirectSearchPaginationResourcesCalculatorImpl#calculateStartPageQueryParams --- error occurred.", ex);
            throw new BusinessProcessingException(EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR,
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage(),
                    ex);
        }
    }

    private Map<String, String> extractQueryParamsFromResourcePage(String resourcePage) throws BusinessProcessingException {

        try {

            // this will contain something like:
            // start=0&count=25&query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29&apikey=6fc2846192484205468e36a35a930f22
            String resourcePageQueryParamsStr1 = resourcePage.split("\\?")[1];

            // this will contain something like:
            // [start=0, count=25,
            // query=authlast%28sgouropoulou%29+or+authfirst%28cleo+c.%29,
            // apikey=6fc2846192484205468e36a35a930f22]
            List<String> resourcePageQueryParamsStr2 = Arrays.asList(resourcePageQueryParamsStr1.split("\\&"));

            // now we have the query params in value-pair (map entry)
            Map<String, String> resourcePageQueryParams = resourcePageQueryParamsStr2
                    .stream()
                    .collect(Collectors.toMap(
                            s -> s.split("=")[0],
                            s -> s.split("=")[1]));

            return resourcePageQueryParams;

        } catch (PatternSyntaxException | IndexOutOfBoundsException ex) {

            log.error("ScienceDirectSearchPaginationResourcesCalculatorImpl#extractQueryParamsFromResourcePage --- error occurred.", ex);
            throw new BusinessProcessingException(EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR,
                    EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage(),
                    ex);

        }
    }
}

package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import java.util.function.Predicate;

import org.springframework.stereotype.Component;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewEntry;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class NoResultsAvailableGuard implements Predicate<ScienceDirectConsumerResultsDto> {

    @Override
    public boolean test(ScienceDirectConsumerResultsDto scienceDirectConsumerResultsDto) {
        return noResultsAvailable(scienceDirectConsumerResultsDto);
    }

    private boolean noResultsAvailable(ScienceDirectConsumerResultsDto scienceDirectConsumerResultsDto) {

        final String forceArrayValueWhenNoResults = "true";
        final String errorValueWhenNoResults = "Result set was empty";

        ScienceDirectSearchViewEntry authorSearchViewEntry = scienceDirectConsumerResultsDto
                .getScienceDirectConsumerSearchViewDto()
                .getEntries()
                .stream()
                .findFirst()
                .get(); //note: here we should not have any problem.

        if (forceArrayValueWhenNoResults.equals(authorSearchViewEntry.getForceArray())
                && errorValueWhenNoResults.equals(authorSearchViewEntry.getError())) {
            log.info("NoResultsAvailableGuard#noResultsAvailable --- no results for provided info.");
            return true;
        }
        return false;
    }
}

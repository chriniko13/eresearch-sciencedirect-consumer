package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerSearchViewDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewEntry;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewQuery;
import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class UniqueEntriesGuard {

    @Value("${perform.unique.entries.guard.reporting}")
    private String performUniqueEntriesGuardReporting;

    @Autowired
    private ObjectFactory<MemoGuardStack> memoGuardStackObjectFactory;

    @Autowired
    private ObjectFactory<MemoResultsReporter> memoResultsReporterObjectFactory;

    @Autowired
    private UniqueEntriesExceptionalRulesRegistry uniqueEntriesExceptionalRulesRegistry;

    public void apply(List<ScienceDirectConsumerResultsDto> results) throws BusinessProcessingException {

        if (noResultsExist(results)) return;

        final MemoGuardStack memoGuardStack = memoGuardStackObjectFactory.getObject();
        final MemoResultsReporter memoResultsReporter = memoResultsReporterObjectFactory.getObject();

        for (int i = 0; i < results.size(); i++) {

            boolean isLastResult = (i == results.size() - 1);

            ScienceDirectConsumerResultsDto result = results.get(i);
            Integer totalUniqueResults = extractTotalUniqueResultsElsevierScopusProvides(result);
            ScienceDirectSearchViewQuery authorSearchViewQuery = result.getScienceDirectConsumerSearchViewDto().getQuery();

            Optional<MemoGuard> memoPeek = memoGuardStack.peek();
            if (memoPeek.isPresent()) { //if stack is not empty.

                Collection<ScienceDirectSearchViewEntry> notUniqueAuthorSearchViewEntries = getNotUniqueAuthorSearchViewEntries(result);
                MemoGuard memoToTestAgainst = new MemoGuard(totalUniqueResults, authorSearchViewQuery, notUniqueAuthorSearchViewEntries);

                if (memoPeek.get().equals(memoToTestAgainst)) {

                    //add the entry...
                    memoGuardStack.push(memoToTestAgainst);

                } else {
                    applyGuard(memoGuardStack, totalUniqueResults, memoResultsReporter, authorSearchViewQuery);

                    //add the new entry...
                    memoGuardStack.push(memoToTestAgainst);
                }

            } else { //if stack is empty.

                //add new entry...
                Collection<ScienceDirectSearchViewEntry> notUniqueAuthorSearchViewEntries = getNotUniqueAuthorSearchViewEntries(result);
                MemoGuard memoGuard = new MemoGuard(totalUniqueResults, authorSearchViewQuery, notUniqueAuthorSearchViewEntries);
                memoGuardStack.push(memoGuard);
            }

            if (isLastResult) {
                applyGuard(memoGuardStack, totalUniqueResults, memoResultsReporter, authorSearchViewQuery);
            }
        }
    }

    private boolean noResultsExist(List<ScienceDirectConsumerResultsDto> results) {
        if (results.size() == 1) {
            ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto = results.get(0).getScienceDirectConsumerSearchViewDto();

            boolean noResults = "0".equals(scienceDirectConsumerSearchViewDto.getTotalResults())
                    && "0".equals(scienceDirectConsumerSearchViewDto.getStartIndex())
                    && "0".equals(scienceDirectConsumerSearchViewDto.getItemsPerPage())
                    && scienceDirectConsumerSearchViewDto.getEntries() != null
                    && scienceDirectConsumerSearchViewDto.getEntries().size() == 1
                    && scienceDirectConsumerSearchViewDto.getEntries().iterator().next().getError().equals("Result set was empty");

            if (noResults) return true;
        }
        return false;
    }

    private void applyGuard(MemoGuardStack memoGuardStack,
                            Integer totalUniqueResults,
                            MemoResultsReporter memoResultsReporter,
                            ScienceDirectSearchViewQuery authorSearchViewQuery) throws BusinessProcessingException {

        //time to accumulate memos...
        Integer accumulateUniqueEntriesSize = memoGuardStack.accumulateUniqueEntriesSize();

        //see if we have the proper results...
        String infoMessage = String.format("UniqueEntriesGuard#apply, accumulateUniqueEntriesSize = %s , elsevierTotalResults = %s.",
                accumulateUniqueEntriesSize, totalUniqueResults);
        log.info(infoMessage);

        if (Boolean.valueOf(performUniqueEntriesGuardReporting)) {
            memoResultsReporter.reportResults(accumulateUniqueEntriesSize, totalUniqueResults, authorSearchViewQuery);
        }

        if (!Objects.equals(accumulateUniqueEntriesSize, totalUniqueResults)) {

            final boolean reportFatalError =
                    uniqueEntriesExceptionalRulesRegistry.toggleOffReportOfFatalErrorIfRuleSatisfied(accumulateUniqueEntriesSize, totalUniqueResults);

            if (reportFatalError) {
                throw new BusinessProcessingException(EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR, EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage());
            }
        }

        //clean stack...
        memoGuardStack.clean();
    }

    private Collection<ScienceDirectSearchViewEntry> getNotUniqueAuthorSearchViewEntries(ScienceDirectConsumerResultsDto result) {
        return result.getScienceDirectConsumerSearchViewDto().getEntries();
    }

    private Integer extractTotalUniqueResultsElsevierScopusProvides(ScienceDirectConsumerResultsDto result) throws BusinessProcessingException {
        return Integer.valueOf(
                Optional.ofNullable(result)
                        .map(ScienceDirectConsumerResultsDto::getScienceDirectConsumerSearchViewDto)
                        .map(ScienceDirectConsumerSearchViewDto::getTotalResults)
                        .orElseThrow(() -> new BusinessProcessingException(EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR, EresearchElsevierScienceDirectConsumerError.BUSINESS_PROCESSING_ERROR.getMessage())));
    }


}

package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import org.springframework.stereotype.Component;

@Component
public class UniqueEntriesExceptionalRulesRegistry {


    public boolean toggleOffReportOfFatalErrorIfRuleSatisfied(Integer accumulateUniqueEntriesSize, Integer totalUniqueResults) {

        boolean reportFatalError = true;

        // -------- FIRST RULE SECTION -------
        //Note: Time to perform some additional validations before we throw an exception, because elsevier api has buggy behaviour...
        //Note: for example:  UniqueEntriesGuard#apply, accumulateUniqueEntriesSize = 5999 , elsevierTotalResults = 50885.
        //Note: we have the above case in author, with author name: {firstname - >S surname -> David}
        if (accumulateUniqueEntriesSize / 10_000 == 0
                && totalUniqueResults / 10_000 != 0) {
            reportFatalError = false;
        }
        // -----------------------------------

        return reportFatalError;
    }

}

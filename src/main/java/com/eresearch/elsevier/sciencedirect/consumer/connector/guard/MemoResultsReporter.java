package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewQuery;

import lombok.extern.log4j.Log4j;

@Log4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class MemoResultsReporter {

    private static final String FILENAME_SUFFIX = "memoResults.txt";
    private static final String DELIMITER = "_";

    @Autowired
    private Clock clock;

    void reportResults(Integer accumulateUniqueEntriesSize, Integer totalUniqueResults, ScienceDirectSearchViewQuery authorSearchViewQuery) {

        try {
            Optional<String> fileName = getFileName();
            if (!fileName.isPresent()) {
                //this will not happen even in our wildest dreams...but just in case to be sure.
                return;
            }

            Path path = Files.createFile(Paths.get(".", fileName.get()));

            try (BufferedWriter bufferedWriter
                         = Files.newBufferedWriter(path, Charset.forName("UTF-8"), StandardOpenOption.DSYNC)) {

                final String elsevierTotalUniqueEntries = "ELSEVIER TOTAL UNIQUE ENTRIES: " + totalUniqueResults;
                final String accumulatedUniqueEntriesSize = "ERESEARCH ACCUMULATED UNIQUE ENTRIES SIZE: " + accumulateUniqueEntriesSize;
                final String providedQueryToElsevier = authorSearchViewQuery.toString();
                final String timestamp = "TIMESTAMP: " + Instant.now(clock).toString();

                bufferedWriter.write(elsevierTotalUniqueEntries);
                bufferedWriter.newLine();
                bufferedWriter.write(accumulatedUniqueEntriesSize);
                bufferedWriter.newLine();
                bufferedWriter.write(providedQueryToElsevier);
                bufferedWriter.newLine();
                bufferedWriter.write(timestamp);
                bufferedWriter.flush();

            }

        } catch (IOException e) {
            log.error("MemoResultsReporter#reportResults, error occured.", e);
        }
    }

    private Optional<String> getFileName() {

        final int noOfRetries = 10;

        String fileName = Thread.currentThread().getName()
                + DELIMITER
                + UUID.randomUUID()
                + DELIMITER
                + FILENAME_SUFFIX;

        int currentRetry = 0;

        while (Files.exists(Paths.get(fileName))
                && (currentRetry++) <= noOfRetries) {

            fileName = Thread.currentThread().getName()
                    + DELIMITER
                    + UUID.randomUUID()
                    + DELIMITER
                    + FILENAME_SUFFIX;

        }
        return Files.exists(Paths.get(fileName)) ? Optional.empty() : Optional.of(fileName);
    }

}

package com.eresearch.elsevier.sciencedirect.consumer.repository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.codahale.metrics.Timer;
import com.eresearch.elsevier.sciencedirect.consumer.dao.ScienceDirectConsumerDao;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerSearchViewDto;
import com.eresearch.elsevier.sciencedirect.consumer.metrics.entries.RepositoryLayerMetricEntry;
import com.eresearch.elsevier.sciencedirect.consumer.repository.extractor.LinkExtractor;
import com.eresearch.elsevier.sciencedirect.consumer.repository.extractor.LinkExtractorRefIdentifier;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.log4j.Log4j;

@Repository
@Log4j
public class ScienceDirectConsumerRepositoryImpl implements ScienceDirectConsumerRepository {

    @Autowired
    private Clock clock;

    @Qualifier("elsevierObjectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    @Qualifier("dbOperationsExecutor")
    private ExecutorService dbOperationsExecutor;

    @Autowired
    private RepositoryLayerMetricEntry repositoryLayerMetricEntry;

    @Autowired
    private ScienceDirectConsumerDao scienceDirectConsumerDao;

    @Autowired
    private LinkExtractor linkExtractor;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Qualifier("transactionTemplate")
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public void save(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto,
                     ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerResultsDto) {

        Runnable task = saveTask(elsevierScienceDirectConsumerDto, elsevierScienceDirectConsumerResultsDto);
        CompletableFuture.runAsync(task, dbOperationsExecutor);
    }

    private Runnable saveTask(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto, ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerResultsDto) {

        return () -> {

            Timer.Context context = repositoryLayerMetricEntry.getRepositoryLayerTimer().time();
            try {

                final String sql = scienceDirectConsumerDao.getInsertQueryForSearchResultsTable();

                final ArrayList<ScienceDirectConsumerResultsDto> resultsToStore = new ArrayList<>(elsevierScienceDirectConsumerResultsDto.getResults());

                final String authorName = objectMapper.writeValueAsString(elsevierScienceDirectConsumerDto);

                final Timestamp creationTimestamp = Timestamp.from(Instant.now(clock));

                this.executeSaveStatements(sql, resultsToStore, authorName, creationTimestamp);

                log.info("ScienceDirectConsumerRepositoryImpl#save --- operation completed successfully.");

            } catch (JsonProcessingException e) {

                log.error("ScienceDirectConsumerRepositoryImpl#save --- error occurred --- not even tx initialized.", e);

            } finally {
                context.stop();
            }
        };

    }

    private void executeSaveStatements(String sql,
                                       ArrayList<ScienceDirectConsumerResultsDto> resultsToStore,
                                       String authorName,
                                       Timestamp creationTimestamp) {
        for (ScienceDirectConsumerResultsDto scienceDirectConsumerResultsDto : resultsToStore) {
            executeSaveStatement(sql, authorName, creationTimestamp, scienceDirectConsumerResultsDto);
        }
    }

    private void executeSaveStatement(String sql,
                                      String authorName,
                                      Timestamp creationTimestamp,
                                      ScienceDirectConsumerResultsDto scienceDirectConsumerResultsDto) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(TransactionStatus txStatus) {

                try {
                    List<String> linksConsumedPerEntry = linkExtractor.extractLinksConsumedFromElsevierApi(
                            scienceDirectConsumerResultsDto,
                            LinkExtractorRefIdentifier.SELF);
                    String linksConsumedPerEntryStr = objectMapper.writeValueAsString(linksConsumedPerEntry);

                    String firstLink = linkExtractor.extractLinkConsumedFromElsevierApi(scienceDirectConsumerResultsDto,
                            LinkExtractorRefIdentifier.FIRST);

                    String lastLink = linkExtractor.extractLinkConsumedFromElsevierApi(scienceDirectConsumerResultsDto,
                            LinkExtractorRefIdentifier.LAST);

                    ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto = scienceDirectConsumerResultsDto.getScienceDirectConsumerSearchViewDto();
                    String scopusConsumerSearchViewDtoStr = objectMapper.writeValueAsString(scienceDirectConsumerSearchViewDto);

                    jdbcTemplate.update(sql,
                            authorName,
                            scopusConsumerSearchViewDtoStr,
                            linksConsumedPerEntryStr,
                            firstLink,
                            lastLink,
                            creationTimestamp);

                } catch (DataAccessException | JsonProcessingException e) {

                    log.error("ScienceDirectConsumerRepositoryImpl#save --- error occurred --- proceeding with rollback.", e);
                    txStatus.setRollbackOnly();
                }
            }
        });

    }
}

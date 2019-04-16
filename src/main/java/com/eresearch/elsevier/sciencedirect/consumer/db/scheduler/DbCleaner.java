package com.eresearch.elsevier.sciencedirect.consumer.db.scheduler;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.eresearch.elsevier.sciencedirect.consumer.dao.ScienceDirectConsumerDao;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class DbCleaner {

    @Autowired
    private Clock clock;

    @Autowired
    private ScienceDirectConsumerDao scienceDirectConsumerDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /*

    NOTE: 1second == 1000milliseconds

     */
    //@Scheduled(initialDelay = 60 * 1000, fixedDelay = 60 * 1000)

    /*

    NOTE:
    crone expression => second, minute, hour, day of month, month, day(s) of week


    ADDITIONAL NOTES:
    1) (*) means match any
    2) * / X means 'every X'
    3) ? ("no specific value") - useful when you need to specify something in one
    of the two fields in which the character is allowed,
    but not the other. For example, if I want my trigger to fire on a particular day
    of the month (say, the 10th), but don't care what day of the week that happens to be,
    I would put "10" in the day-of-month field, and "?" in the day-of-week field.

        +-------------------- second (0 - 59)
        |  +----------------- minute (0 - 59)
        |  |  +-------------- hour (0 - 23)
        |  |  |  +----------- day of month (1 - 31)
        |  |  |  |  +-------- month (1 - 12)
        |  |  |  |  |  +----- day of week (0 - 6) (Sunday=0 or 7)
        |  |  |  |  |  |  +-- year [optional]
        |  |  |  |  |  |  |
        *  *  *  *  *  *  * command to be executed

     */
    @Scheduled(cron = "0 0/10 * * * *")
    //every ten minutes, but to be safe for transactions, multi-access, etc., will should do it off-days.
    public void dbCleanerTask() {

        final ZonedDateTime now = ZonedDateTime.now(clock);

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                try {

                    log.info("DbCleaner#dbCleanerTask will clean the database now [time=" + now + "].");
                    jdbcTemplate.update(scienceDirectConsumerDao.getDeleteQueryForSearchResultsTable());
                    jdbcTemplate.update(scienceDirectConsumerDao.getResetAutoIncrementForSearchResultsTable());

                } catch (DataAccessException e) {

                    log.error("DbCleaner#dbCleanerTask, could not execute cleaning task (rollback) [time=" + now + "].", e);
                    transactionStatus.setRollbackOnly();

                }
            }
        });
    }
}

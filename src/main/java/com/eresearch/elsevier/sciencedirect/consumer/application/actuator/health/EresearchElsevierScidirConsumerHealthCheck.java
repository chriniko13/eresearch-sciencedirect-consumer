package com.eresearch.elsevier.sciencedirect.consumer.application.actuator.health;

import com.eresearch.elsevier.sciencedirect.consumer.dao.ScienceDirectConsumerDao;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.service.ElsevierScienceDirectConsumerService;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class EresearchElsevierScidirConsumerHealthCheck extends AbstractHealthIndicator {

    @Qualifier("hikariDataSource")
    @Autowired
    private HikariDataSource hikariDataSource;

    @Autowired
    private ScienceDirectConsumerDao scienceDirectConsumerDao;

    @Autowired
    private ElsevierScienceDirectConsumerService elsevierScienceDirectConsumerService;

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${do.specific.scidir.api.health.check}")
    private String doSpecificScidirApiHealthCheck;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        this.performBasicHealthChecks();

        Optional<Exception> ex = this.specificHealthCheck();

        if (ex.isPresent()) {
            builder.down(ex.get());
        } else {
            builder.up();
        }
    }

    private void performBasicHealthChecks() {
        //check disk...
        DiskSpaceHealthIndicatorProperties diskSpaceHealthIndicatorProperties
                = new DiskSpaceHealthIndicatorProperties();
        diskSpaceHealthIndicatorProperties.setThreshold(10737418240L); /*10 GB*/
        new DiskSpaceHealthIndicator(diskSpaceHealthIndicatorProperties);

        //check datasource...
        new DataSourceHealthIndicator(hikariDataSource);

        //check jms (active mq) is up...
        new JmsHealthIndicator(jmsTemplate.getConnectionFactory());
    }

    private Optional<Exception> specificHealthCheck() {
        //check if required table(s) exist...
        Optional<Exception> ex1 = this.specificDbHealthCheck();
        if (ex1.isPresent()) {
            return ex1;
        }

        if (Boolean.valueOf(doSpecificScidirApiHealthCheck)) {
            //check if we can get a response from elsevier-api...
            Optional<Exception> ex2 = specificSciDirApiHealthCheck();
            if (ex2.isPresent()) {
                return ex2;
            }
        }

        return Optional.empty();
    }

    private Optional<Exception> specificDbHealthCheck() {
        if (Objects.isNull(hikariDataSource)) {
            log.error("EresearchElsevierScidirConsumerHealthCheck#specificDbHealthCheck --- hikariDataSource is null.");
            return Optional.of(new NullPointerException("hikariDataSource is null."));
        }

        JdbcTemplate jdbcTemplate = new JdbcTemplate(hikariDataSource);

        try {
            jdbcTemplate.execute(scienceDirectConsumerDao.getSelectQueryForSearchResultsTable());
        } catch (DataAccessException ex) {
            log.error("EresearchElsevierScidirConsumerHealthCheck#specificDbHealthCheck --- db is in bad state.", ex);
            return Optional.of(ex);
        }

        return Optional.empty();
    }

    private Optional<Exception> specificSciDirApiHealthCheck() {

        try {

            ElsevierScienceDirectConsumerResultsDto results = elsevierScienceDirectConsumerService.elsevierScienceDirectConsumerOperation(
                    ElsevierScienceDirectConsumerDto
                            .builder()
                            .firstname("Christos")
                            .surname("Skourlas")
                            .build());

            if (Objects.isNull(results)) {
                log.error("EresearchElsevierScidirConsumerHealthCheck#specificSciDirApiHealthCheck --- result from elsevier-scidir-api is null.");
                return Optional.of(new NullPointerException("result from elsevier-scidir-api is null."));
            }

        } catch (BusinessProcessingException ex) {
            log.error("EresearchElsevierScidirConsumerHealthCheck#specificSciDirApiHealthCheck --- communication issue.", ex);
            return Optional.of(ex);
        }

        return Optional.empty();
    }
}

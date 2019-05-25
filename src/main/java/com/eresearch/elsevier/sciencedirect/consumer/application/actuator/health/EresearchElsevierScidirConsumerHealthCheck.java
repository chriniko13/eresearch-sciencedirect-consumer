package com.eresearch.elsevier.sciencedirect.consumer.application.actuator.health;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.service.ElsevierScienceDirectConsumerService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.*;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Log4j
@Component
public class EresearchElsevierScidirConsumerHealthCheck extends AbstractHealthIndicator {

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

        //check jms (active mq) is up...
        new JmsHealthIndicator(jmsTemplate.getConnectionFactory());
    }

    private Optional<Exception> specificHealthCheck() {

        if (Boolean.valueOf(doSpecificScidirApiHealthCheck)) {
            //check if we can get a response from elsevier-api...
            Optional<Exception> ex2 = specificSciDirApiHealthCheck();
            if (ex2.isPresent()) {
                return ex2;
            }
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

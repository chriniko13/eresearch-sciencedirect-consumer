package com.eresearch.elsevier.sciencedirect.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import com.eresearch.elsevier.sciencedirect.consumer.application.event.listener.ApplicationEnvironmentPreparedEventListener;
import com.eresearch.elsevier.sciencedirect.consumer.application.event.listener.ApplicationFailedEventListener;
import com.eresearch.elsevier.sciencedirect.consumer.application.event.listener.ApplicationReadyEventListener;
import com.eresearch.elsevier.sciencedirect.consumer.application.event.listener.ApplicationStartedEventListener;
import com.eresearch.elsevier.sciencedirect.consumer.application.event.listener.BaseApplicationEventListener;
import com.eresearch.elsevier.sciencedirect.consumer.db.DbOperations;

/**
 * This is the entry point of our microservice.
 *
 * @author chriniko
 */

@SpringBootApplication
public class EresearchElsevierSciencedirectConsumerApplication implements CommandLineRunner, ApplicationRunner {

    public static void main(String[] args) {
        SpringApplicationBuilder springApplicationBuilder
                = new SpringApplicationBuilder(EresearchElsevierSciencedirectConsumerApplication.class);

        registerApplicationListeners(springApplicationBuilder);

        springApplicationBuilder
                .web(true)
                .run(args);
    }

    private static void registerApplicationListeners(final SpringApplicationBuilder springApplicationBuilder) {
        springApplicationBuilder.listeners(new ApplicationEnvironmentPreparedEventListener());
        springApplicationBuilder.listeners(new ApplicationFailedEventListener());
        springApplicationBuilder.listeners(new ApplicationReadyEventListener());
        springApplicationBuilder.listeners(new ApplicationStartedEventListener());
        springApplicationBuilder.listeners(new BaseApplicationEventListener());
    }

    @Autowired
    private DbOperations dbOperations;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //Note: add your scenarios if needed.
    }

    @Override
    public void run(String... args) throws Exception {
        dbOperations.runTask();
    }
}

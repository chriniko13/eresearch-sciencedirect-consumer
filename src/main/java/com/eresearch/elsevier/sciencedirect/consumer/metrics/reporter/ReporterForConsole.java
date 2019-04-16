package com.eresearch.elsevier.sciencedirect.consumer.metrics.reporter;

import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Clock;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.MetricRegistry;

@Component
public class ReporterForConsole {

    private static final long TIME_BETWEEN_REPORTING_POLLS = 10;

    @Qualifier("appMetricRegistry")
    @Autowired
    private MetricRegistry metricRegistry;

    @Autowired
    private java.time.Clock clock;

    @Value("${console.reporter.enabled}")
    private String enabled;

    @PostConstruct
    public void init() {
        if (Boolean.valueOf(enabled)) {
            startReport();
        }
    }

    private void startReport() {

        Clock clockForMetrics = Clock.defaultClock();

        ConsoleReporter reporter = ConsoleReporter
                .forRegistry(metricRegistry)
                .formattedFor(Locale.ENGLISH)
                .withClock(clockForMetrics)
                .formattedFor(TimeZone.getTimeZone(clock.getZone()))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .outputTo(System.out) //TODO change it in the future...
                .build();

        reporter.start(TIME_BETWEEN_REPORTING_POLLS, TimeUnit.SECONDS);
    }


}

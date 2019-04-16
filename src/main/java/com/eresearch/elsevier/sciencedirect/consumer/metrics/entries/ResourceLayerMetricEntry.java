package com.eresearch.elsevier.sciencedirect.consumer.metrics.entries;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.eresearch.elsevier.sciencedirect.consumer.resource.ElsevierScienceDirectConsumerResource;

@Component
public class ResourceLayerMetricEntry {

    @Qualifier("appMetricRegistry")
    @Autowired
    private MetricRegistry metricRegistry;

    private Counter successResourceLayerCounter;

    private Counter failureResourceLayerCounter;

    private Timer resourceLayerTimer;

    @PostConstruct
    public void init() {
        registerCounters();
        registerTimers();
    }

    private void registerTimers() {

        String timerName = MetricRegistry.name(ElsevierScienceDirectConsumerResource.class,
                "elsevierScienceDirectConsumerOperation",
                "timer");
        resourceLayerTimer = metricRegistry.timer(timerName);
    }

    private void registerCounters() {

        String counterSuccessName = MetricRegistry.name(ElsevierScienceDirectConsumerResource.class,
                "elsevierScienceDirectConsumerOperation", "counter", "success");

        String counterFailureName = MetricRegistry.name(ElsevierScienceDirectConsumerResource.class,
                "elsevierScienceDirectConsumerOperation", "counter", "failure");

        successResourceLayerCounter = metricRegistry.counter(counterSuccessName);
        failureResourceLayerCounter = metricRegistry.counter(counterFailureName);
    }

    public Counter getFailureResourceLayerCounter() {
        return failureResourceLayerCounter;
    }

    public Counter getSuccessResourceLayerCounter() {
        return successResourceLayerCounter;
    }

    public Timer getResourceLayerTimer() {
        return resourceLayerTimer;
    }
}

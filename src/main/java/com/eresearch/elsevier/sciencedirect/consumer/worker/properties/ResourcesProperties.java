package com.eresearch.elsevier.sciencedirect.consumer.worker.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "resources")
public class ResourcesProperties {
    private String splitterThreshold;
    private String listChoppedSize;
}

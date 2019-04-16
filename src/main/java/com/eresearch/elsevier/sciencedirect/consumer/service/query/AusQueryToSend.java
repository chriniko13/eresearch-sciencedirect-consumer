package com.eresearch.elsevier.sciencedirect.consumer.service.query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("aus")
public class AusQueryToSend implements QueryToSend {

    @Value("${query.aus.enabled}")
    private String shouldSend;

    @Override
    public String get(String authorName) {
        return "aus(" + authorName + ")&view=complete";
    }

    @Override
    public boolean shouldSend() {
        return Boolean.valueOf(shouldSend);
    }
}

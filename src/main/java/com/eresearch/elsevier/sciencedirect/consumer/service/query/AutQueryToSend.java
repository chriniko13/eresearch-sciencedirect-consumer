package com.eresearch.elsevier.sciencedirect.consumer.service.query;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("aut")
public class AutQueryToSend implements QueryToSend {

    @Value("${query.aut.enabled}")
    private String shouldSend;

    @Override
    public String get(String authorName) {
        return "aut(" + authorName + ")&view=complete";
    }

    @Override
    public boolean shouldSend() {
        return Boolean.valueOf(shouldSend);
    }
}

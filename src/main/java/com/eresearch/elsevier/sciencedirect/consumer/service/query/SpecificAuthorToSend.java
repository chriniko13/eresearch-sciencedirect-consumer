package com.eresearch.elsevier.sciencedirect.consumer.service.query;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("specific-author")
public class SpecificAuthorToSend implements QueryToSend {

    @Value("${query.specificauthor.enabled}")
    private String shouldSend;

    @Override
    public String get(String authorName) {
        return "specific-author(" + authorName + ")&view=complete";
    }

    @Override
    public boolean shouldSend() {
        return Boolean.valueOf(shouldSend);
    }
}

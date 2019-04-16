package com.eresearch.elsevier.sciencedirect.consumer.service.query;


public interface QueryToSend {

    String get(String authorName);

    boolean shouldSend();
}

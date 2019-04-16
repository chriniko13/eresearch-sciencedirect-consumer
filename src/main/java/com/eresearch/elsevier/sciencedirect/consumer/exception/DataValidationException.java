package com.eresearch.elsevier.sciencedirect.consumer.exception;


import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DataValidationException extends Exception {

    private static final long serialVersionUID = -4807690929767265288L;

    private final EresearchElsevierScienceDirectConsumerError eresearchElsevierScienceDirectConsumerError;

    public DataValidationException(EresearchElsevierScienceDirectConsumerError eresearchElsevierScienceDirectConsumerError, String message) {
        super(message);
        this.eresearchElsevierScienceDirectConsumerError = eresearchElsevierScienceDirectConsumerError;
    }

    public EresearchElsevierScienceDirectConsumerError getEresearchElsevierScienceDirectConsumerError() {
        return eresearchElsevierScienceDirectConsumerError;
    }
}

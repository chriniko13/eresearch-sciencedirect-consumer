package com.eresearch.elsevier.sciencedirect.consumer.exception;

import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class BusinessProcessingException extends Exception {

	private static final long serialVersionUID = -4352150800142237666L;

	private final EresearchElsevierScienceDirectConsumerError eresearchElsevierScienceDirectConsumerError;

	public BusinessProcessingException(EresearchElsevierScienceDirectConsumerError eresearchElsevierScienceDirectConsumerError, String message, Throwable cause) {
		super(message, cause);
		this.eresearchElsevierScienceDirectConsumerError = eresearchElsevierScienceDirectConsumerError;
	}

	public BusinessProcessingException(EresearchElsevierScienceDirectConsumerError eresearchElsevierScienceDirectConsumerError, String message) {
		super(message);
		this.eresearchElsevierScienceDirectConsumerError = eresearchElsevierScienceDirectConsumerError;
	}

	public EresearchElsevierScienceDirectConsumerError getEresearchElsevierScienceDirectConsumerError() {
		return eresearchElsevierScienceDirectConsumerError;
	}
}

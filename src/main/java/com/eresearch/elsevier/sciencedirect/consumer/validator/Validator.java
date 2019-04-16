package com.eresearch.elsevier.sciencedirect.consumer.validator;

import com.eresearch.elsevier.sciencedirect.consumer.exception.DataValidationException;

public interface Validator<T> {

    void validate(T data) throws DataValidationException;
}

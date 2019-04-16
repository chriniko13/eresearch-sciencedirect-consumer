package com.eresearch.elsevier.sciencedirect.consumer.validator;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.error.EresearchElsevierScienceDirectConsumerError;
import com.eresearch.elsevier.sciencedirect.consumer.exception.DataValidationException;
import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

/*
NOTE: only initials can be null or empty.
 */
@Component
@Log4j
public class ElsevierScienceDirectConsumerDtoValidator implements Validator<ElsevierScienceDirectConsumerDto> {

    @Override
    public void validate(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto) throws DataValidationException {

        // first validation...
        if (Objects.isNull(elsevierScienceDirectConsumerDto)) {

            log.error("ElsevierScienceDirectConsumerDtoValidator#validate --- error occurred (first validation) --- elsevierScienceDirectConsumerDto = " + elsevierScienceDirectConsumerDto);
            throw new DataValidationException(EresearchElsevierScienceDirectConsumerError.DATA_VALIDATION_ERROR,
                    EresearchElsevierScienceDirectConsumerError.DATA_VALIDATION_ERROR.getMessage());
        }

        // second validation...
        if (orReducer(
                isNull(elsevierScienceDirectConsumerDto.getFirstname()),
                isNull(elsevierScienceDirectConsumerDto.getSurname()),
                isEmpty(elsevierScienceDirectConsumerDto.getFirstname()),
                isEmpty(elsevierScienceDirectConsumerDto.getSurname()))) {

            log.error("ElsevierScienceDirectConsumerDtoValidator#validate --- error occurred (second validation) --- elsevierScienceDirectConsumerDto = " + elsevierScienceDirectConsumerDto);
            throw new DataValidationException(EresearchElsevierScienceDirectConsumerError.DATA_VALIDATION_ERROR,
                    EresearchElsevierScienceDirectConsumerError.DATA_VALIDATION_ERROR.getMessage());
        }
    }

    private Boolean orReducer(Boolean... booleans) {
        return Arrays.stream(booleans).reduce(false, (acc, elem) -> acc || elem);
    }

    private Boolean isEmpty(String datum) {
        return "".equals(datum);
    }

    private <T> Boolean isNull(T datum) {
        return Objects.isNull(datum);
    }

}

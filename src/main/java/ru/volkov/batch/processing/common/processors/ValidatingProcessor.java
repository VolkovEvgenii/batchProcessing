package ru.volkov.batch.processing.common.processors;

import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import ru.volkov.batch.processing.domain.Customer;

public class ValidatingProcessor implements Validator<Customer> {

    @Override
    public void validate(Customer customer) throws ValidationException {
        if(customer.getName().contains("10")) {
            throw new ValidationException("customer contains 10");
        }
    }
}

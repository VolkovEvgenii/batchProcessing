package ru.volkov.batch.processing.filtering;

import org.springframework.batch.item.ItemProcessor;
import ru.volkov.batch.processing.domain.Customer;

public class FilteringItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        if (customer.getId() % 2 == 0) {
            return customer;
        } else {
            return null;
        }
    }
}

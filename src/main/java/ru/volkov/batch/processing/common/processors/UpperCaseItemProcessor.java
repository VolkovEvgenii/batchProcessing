package ru.volkov.batch.processing.common.processors;

import org.springframework.batch.item.ItemProcessor;
import ru.volkov.batch.processing.domain.Customer;

public class UpperCaseItemProcessor implements ItemProcessor<Customer, Customer> {

    @Override
    public Customer process(Customer customer) throws Exception {
        Customer newCustomer = new Customer();
        newCustomer.setId(customer.getId());
        newCustomer.setName(customer.getName().toUpperCase());
        newCustomer.setDate(customer.getDate());
        return newCustomer;
    }
}

package ru.volkov.batch.processing.common;

import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.domain.Customer;
import ru.volkov.batch.processing.domain.CustomerRowMapper;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class JdbcReader {

    private DataSource dataSource;

    public JdbcReader(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public JdbcPagingItemReader<Customer> getJdbcPagingItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(this.dataSource);
        reader.setRowMapper(new CustomerRowMapper());

        PostgresPagingQueryProvider provider = new PostgresPagingQueryProvider();
        provider.setSelectClause("id, name, date");
        provider.setFromClause("output.customers");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        provider.setSortKeys(sortKeys);

        reader.setQueryProvider(provider);

        return reader;
    }
}

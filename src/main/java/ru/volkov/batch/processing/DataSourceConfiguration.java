package ru.volkov.batch.processing;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .url("jdbc:postgresql://localhost:5432/batchess")
                .driverClassName("org.postgresql.Driver")
                .username("postgres")
                .password("postgres")
                .build();
    }
}

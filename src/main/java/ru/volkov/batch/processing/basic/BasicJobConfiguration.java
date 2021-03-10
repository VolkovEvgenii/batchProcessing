package ru.volkov.batch.processing.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.UpperCaseItemProcessor;
import ru.volkov.batch.processing.domain.Customer;

@Configuration
public class BasicJobConfiguration extends DefaultBatchConfigurer {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private ItemReader<Customer> jdbcItemReader;
    private ItemWriter<Customer> xmlWriter;

    public BasicJobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            @Qualifier("jdbcItemReader") ItemReader<Customer> jdbcItemReader,
            @Qualifier("xmlWriter") ItemWriter<Customer> xmlWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.jdbcItemReader = jdbcItemReader;
        this.xmlWriter = xmlWriter;
    }

    @Bean
    public ItemProcessor<Customer, Customer> upperCaseProcessor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    public Step basicStep() throws Exception {
        return stepBuilderFactory.get("basicStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcItemReader)
                .processor(upperCaseProcessor())
                .writer(xmlWriter)
                .build();

    }

    @Bean
    public Job basicJob() throws Exception {
        return jobBuilderFactory.get("basicJob")
                .start(basicStep())
                .build();
    }



}

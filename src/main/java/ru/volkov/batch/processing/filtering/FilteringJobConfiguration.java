package ru.volkov.batch.processing.filtering;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.FilteringItemProcessor;
import ru.volkov.batch.processing.common.writers.XmlWriterConfiguration;
import ru.volkov.batch.processing.domain.Customer;

@Configuration
public class FilteringJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private ItemReader<Customer> jdbcItemReader;
    private ItemWriter<Customer> xmlWriter;

    public FilteringJobConfiguration(
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
    public FilteringItemProcessor filteringProcessor() {
        return new FilteringItemProcessor();
    }

    @Bean

    public Step filteringStep() throws Exception {
        return stepBuilderFactory.get("filteringStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcItemReader)
                .processor(filteringProcessor())
                .writer(xmlWriter)
                .build();

    }

    @Bean
    public Job filteringJob() throws Exception {
        return jobBuilderFactory.get("filteringJob")
                .start(filteringStep())
                .build();
    }
}

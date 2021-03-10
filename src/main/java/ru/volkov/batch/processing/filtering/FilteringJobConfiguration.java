package ru.volkov.batch.processing.filtering;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.FilteringItemProcessor;
import ru.volkov.batch.processing.common.readers.JdbcReader;
import ru.volkov.batch.processing.common.writers.XmlWriter;
import ru.volkov.batch.processing.domain.Customer;

@Configuration
public class FilteringJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private JdbcReader jdbcReader;
    private XmlWriter xmlWriter;

    public FilteringJobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            JdbcReader jdbcReader,
            XmlWriter xmlWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.jdbcReader = jdbcReader;
        this.xmlWriter = xmlWriter;
    }
    @Bean
    public ItemProcessor<Customer, Customer> filteringProcessor() {
        return new FilteringItemProcessor();
    }

    @Bean
    public Step filteringStep() throws Exception {
        return stepBuilderFactory.get("filteringStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcReader.getJdbcPagingItemReader())
                .processor(filteringProcessor())
                .writer(xmlWriter.getXmlItemWriter())
                .build();

    }

    @Bean
    public Job filteringJob() throws Exception {
        return jobBuilderFactory.get("filteringJob")
                .start(filteringStep())
                .build();
    }
}

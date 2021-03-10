package ru.volkov.batch.processing.composite;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.FilteringItemProcessor;
import ru.volkov.batch.processing.common.processors.UpperCaseItemProcessor;
import ru.volkov.batch.processing.common.writers.XmlWriterConfiguration;
import ru.volkov.batch.processing.domain.Customer;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class CompositeJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private ItemReader<Customer> jdbcItemReader;
    private ItemWriter<Customer> xmlWriter;

    public CompositeJobConfiguration(
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
    public CompositeItemProcessor<Customer, Customer> compositeProcessor() {
        List<ItemProcessor<Customer, Customer>> processors = new ArrayList<>();
        processors.add(new FilteringItemProcessor());
        processors.add(new UpperCaseItemProcessor());

        CompositeItemProcessor<Customer, Customer> compositeItemProcessor = new CompositeItemProcessor<>();
        compositeItemProcessor.setDelegates(processors);
        return compositeItemProcessor;
    }

    @Bean
    public Step compositeStep() throws Exception {
        return stepBuilderFactory.get("compositeStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcItemReader)
                .processor(compositeProcessor())
                .writer(xmlWriter)
                .build();

    }

    @Bean
    public Job compositeJob() throws Exception {
        return jobBuilderFactory.get("compositeJob")
                .start(compositeStep())
                .build();
    }
}

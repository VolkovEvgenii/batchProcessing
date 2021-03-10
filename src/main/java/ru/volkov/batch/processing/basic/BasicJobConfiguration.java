package ru.volkov.batch.processing.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.JdbcReader;
import ru.volkov.batch.processing.common.XmlWriter;
import ru.volkov.batch.processing.domain.Customer;

@Configuration
public class BasicJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private JdbcReader jdbcReader;
    private XmlWriter xmlWriter;

    public BasicJobConfiguration(
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
    public ItemProcessor<Customer, Customer> upperCaseProcessor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    public Step basicStep() throws Exception {
        return stepBuilderFactory.get("basicStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcReader.getJdbcPagingItemReader())
                .processor(upperCaseProcessor())
                .writer(xmlWriter.getXmlItemWriter())
                .build();

    }

    @Bean
    public Job basicJob() throws Exception {
        return jobBuilderFactory.get("basicJob")
                .start(basicStep())
                .build();
    }


}

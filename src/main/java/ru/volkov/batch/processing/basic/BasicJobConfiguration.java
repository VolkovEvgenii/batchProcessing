package ru.volkov.batch.processing.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.UpperCaseItemProcessor;
import ru.volkov.batch.processing.common.readers.JdbcReader;
import ru.volkov.batch.processing.common.writers.XmlWriter;
import ru.volkov.batch.processing.domain.Customer;

import javax.sql.DataSource;

@Configuration
public class BasicJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private DataSource dataSource;

    public BasicJobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public ItemProcessor<Customer, Customer> upperCaseProcessor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    public JdbcPagingItemReader<Customer> jdbcReader() {
        JdbcReader reader = new JdbcReader(this.dataSource);
        return reader.init();
    }

    @Bean
    public StaxEventItemWriter<Customer> xmlWriter() throws Exception {
        XmlWriter writer = new XmlWriter();
        return writer.init();
    }

    @Bean
    public Step basicStep() throws Exception {
        return stepBuilderFactory.get("basicStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcReader())
                .processor(upperCaseProcessor())
                .writer(xmlWriter())
                .build();

    }

    @Bean
    public Job basicJob() throws Exception {
        return jobBuilderFactory.get("basicJob")
                .start(basicStep())
                .build();
    }


}

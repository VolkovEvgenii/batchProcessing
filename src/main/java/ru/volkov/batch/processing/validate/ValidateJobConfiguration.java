package ru.volkov.batch.processing.validate;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.processors.ValidatingProcessor;
import ru.volkov.batch.processing.common.readers.JdbcReader;
import ru.volkov.batch.processing.common.writers.XmlWriter;
import ru.volkov.batch.processing.domain.Customer;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ru.volkov.batch.processing.common")
public class ValidateJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private DataSource dataSource;

    public ValidateJobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    public ValidatingItemProcessor<Customer> validateProcessor() {
        ValidatingItemProcessor<Customer> processor = new ValidatingItemProcessor<>();
        processor.setValidator(new ValidatingProcessor());
        processor.setFilter(true);
        return processor;
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
    public Step validatingStep() throws Exception {
        return stepBuilderFactory.get("validatingStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcReader())
                .processor(validateProcessor())
                .writer(xmlWriter())
                .build();
    }

    @Bean
    public Job validatingJob() throws Exception {
        return jobBuilderFactory.get("validatingJob")
                .start(validatingStep())
                .build();
    }
}

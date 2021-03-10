package ru.volkov.batch.processing.filtering;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.volkov.batch.processing.common.JdbcReader;
import ru.volkov.batch.processing.common.XmlWriter;
import ru.volkov.batch.processing.domain.Customer;

import javax.sql.DataSource;

@Configuration
public class FilteringJobConfiguration {

    private StepBuilderFactory stepBuilderFactory;
    private JobBuilderFactory jobBuilderFactory;
    private DataSource dataSource;

    public FilteringJobConfiguration(
            StepBuilderFactory stepBuilderFactory,
            JobBuilderFactory jobBuilderFactory,
            DataSource dataSource) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.dataSource = dataSource;
    }

    @Bean
    @Qualifier("filteringXmlItemWriting")
    public StaxEventItemWriter<Customer> xmlItemWriter() throws Exception {
        XmlWriter writer = new XmlWriter();
        return writer.getXmlItemWriter();
    }

    @Bean
    public ItemProcessor<Customer, Customer> processor() {
        return new FilteringItemProcessor();
    }

    @Bean
    public JdbcPagingItemReader<Customer> reader() {
        JdbcReader reader = new JdbcReader(this.dataSource);
        return reader.getJdbcPagingItemReader();
    }

    @Bean
    public Step filteringStep() throws Exception {
        return stepBuilderFactory.get("filteringStep")
                .<Customer, Customer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(xmlItemWriter())
                .build();

    }

    @Bean
    public Job filteringJob() throws Exception {
        return jobBuilderFactory.get("filteringJob")
                .start(filteringStep())
                .build();
    }
}

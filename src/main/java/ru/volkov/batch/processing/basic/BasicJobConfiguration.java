package ru.volkov.batch.processing.basic;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import ru.volkov.batch.processing.domain.Customer;
import ru.volkov.batch.processing.domain.CustomerRowMapper;

import javax.sql.DataSource;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    public JdbcPagingItemReader<Customer> jdbcPagingItemReader() {
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

    @Bean
    public StaxEventItemWriter<Customer> xmlItemWriter() throws Exception {

        XStreamMarshaller marshaller = new XStreamMarshaller();
        Map<String, Class> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);
        marshaller.setAliases(aliases);

        StaxEventItemWriter<Customer> writer = new StaxEventItemWriter<>();
        writer.setRootTagName("customers");
        writer.setMarshaller(marshaller);
        String outputPath = File.createTempFile("xmlCustomers", ".xml").getAbsolutePath();
        System.out.println(">>> Output path -'{" + outputPath + "'}");
        writer.setResource(new FileSystemResource(outputPath));

        writer.afterPropertiesSet();
        return writer;
    }

    @Bean
    public ItemProcessor<Customer, Customer> processor() {
        return new UpperCaseItemProcessor();
    }

    @Bean
    public Step basicStep() throws Exception {
        return stepBuilderFactory.get("basicStep")
                .<Customer, Customer>chunk(10)
                .reader(jdbcPagingItemReader())
                .processor(processor())
                .writer(xmlItemWriter())
                .build();

    }

    @Bean
    public Job basicJob() throws Exception {
        return jobBuilderFactory.get("basicJob")
                .start(basicStep())
                .build();
    }


}

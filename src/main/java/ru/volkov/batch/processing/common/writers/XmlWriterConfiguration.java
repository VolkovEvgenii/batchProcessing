package ru.volkov.batch.processing.common.writers;

import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;
import ru.volkov.batch.processing.domain.Customer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class XmlWriterConfiguration {

    @Bean
    @Qualifier("xmlWriter")
    @Scope("prototype")
    public StaxEventItemWriter<Customer> xmlWriter() throws Exception {

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
}

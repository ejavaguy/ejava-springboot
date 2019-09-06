package info.ejava.examples.svc.content.quotes;

import com.fasterxml.jackson.databind.SerializationFeature;
import info.ejava.examples.svc.content.quotes.dto.ISODateFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@SpringBootApplication
public class QuotesApplication {
    public static void main(String...args) {
        SpringApplication.run(QuotesApplication.class, args);
    }

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder()
                .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .dateFormat(new ISODateFormat());
    }
}

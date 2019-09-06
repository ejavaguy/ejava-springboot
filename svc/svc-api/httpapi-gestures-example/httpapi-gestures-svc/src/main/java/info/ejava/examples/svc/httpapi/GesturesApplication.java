package info.ejava.examples.svc.httpapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@Slf4j
public class GesturesApplication {
    public static void main(String...args) {
        SpringApplication.run(GesturesApplication.class, args);
    }


    @Bean
    public Filter logFilter() {
        final List<String> headers = Arrays.asList(
                "accept,host,content-length,Content-Type,accept-encoding"
                        .toLowerCase().split(","));
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(1000);
        filter.setBeforeMessagePrefix(System.lineSeparator());
        filter.setAfterMessagePrefix(System.lineSeparator());
        filter.setIncludeHeaders(true);
        filter.setHeaderPredicate(h->headers.contains(h));
        return filter;
    }

}

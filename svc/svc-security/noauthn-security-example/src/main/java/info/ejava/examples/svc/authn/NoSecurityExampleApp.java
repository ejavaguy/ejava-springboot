package info.ejava.examples.svc.authn;

import info.ejava.examples.common.web.WebLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication
public class NoSecurityExampleApp {

	public static void main(String[] args) {
		SpringApplication.run(NoSecurityExampleApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}
}

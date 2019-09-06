package info.ejava.examples.svc.authz.authorities;

import info.ejava.examples.common.web.WebLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication
public class AuthoritiesExampleApp {

	public static void main(String[] args) {
		SpringApplication.run(AuthoritiesExampleApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}
}

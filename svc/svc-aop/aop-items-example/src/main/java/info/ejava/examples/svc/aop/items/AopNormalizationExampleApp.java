package info.ejava.examples.svc.aop.items;

import info.ejava.examples.common.web.WebLoggingFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import javax.servlet.Filter;

@SpringBootApplication
@EnableAspectJAutoProxy
public class AopNormalizationExampleApp {

	public static void main(String[] args) {
		SpringApplication.run(AopNormalizationExampleApp.class, args);
	}

	@Bean
	public Filter logFilter() {
		return WebLoggingFilter.logFilter();
	}
}

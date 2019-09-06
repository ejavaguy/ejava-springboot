package info.ejava.examples.svc.docker.votes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.annotation.PostConstruct;
import java.util.Arrays;

@SpringBootApplication
@EnableAspectJAutoProxy
@Slf4j
public class VotesExampleApp {
	@Value("${spring.datasource.username:}")
	private String postgresUser;
	@Value("${spring.datasource.password:}")
	private String postgresPassword;

	public static void main(String[] args) {
		log.debug("{}", Arrays.asList(args));
		SpringApplication.run(VotesExampleApp.class, args);
	}

	@PostConstruct
	public void init() {
		log.info("postgres credentials={}/{}", postgresUser, postgresPassword);
	}

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
				.indentOutput(true)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return builder;
	}

	@Bean
	public ObjectMapper jsonMapper(Jackson2ObjectMapperBuilder builder) {
		return builder.createXmlMapper(false).build();
	}
}

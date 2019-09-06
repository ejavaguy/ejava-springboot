package info.ejava.examples.svc.content;

import com.fasterxml.jackson.databind.SerializationFeature;
import info.ejava.examples.common.time.ISODateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.servlet.Filter;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

@SpringBootApplication
public class JacksonLeniencyApplication {
	@RestController
	@Slf4j
	public static class DateEcho {
		/**
		 * Echo back the POJO instance received
		 */
		@PostMapping(value = "/api/dates",
				consumes = {MediaType.APPLICATION_JSON_VALUE},
				produces = {MediaType.APPLICATION_JSON_VALUE})
		public ADate echo(@RequestBody ADate dates) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSX");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			log.info("{} {}", dates, sdf.format(dates.getDate()));
			return dates;
		}

		/**
		 * Echo back the JSON string received
		 */
		@PostMapping(value = "/api/dates/string",
				consumes = {MediaType.APPLICATION_JSON_VALUE},
				produces = {MediaType.TEXT_PLAIN_VALUE})
		public String echo(@RequestBody String dates) {
			log.info("{}", dates);
			return dates;
		}

		/**
		 * Return the date(s) converted from EST to UTC to see how
		 * the different TZ marshalled
		 */
		@PostMapping(value = "/api/dates/est2utc",
				consumes = {MediaType.APPLICATION_JSON_VALUE},
				produces = {MediaType.APPLICATION_JSON_VALUE})
		public ADate est2utc(@RequestBody ADate dates) {
			log.info("{}", dates);
			ZonedDateTime utc = ZonedDateTime.ofInstant(dates.getInstant(), ZoneId.of("UTC"));
			return ADate.of(utc);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(JacksonLeniencyApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		RestTemplate restTemplate = builder.requestFactory(
				//used to read the streams twice
				()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
				.build();
		List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
		if (CollectionUtils.isEmpty(interceptors)) {
			interceptors = new ArrayList<>();
		}
		interceptors.add(new RestTemplateLoggingFilter());
		restTemplate.setInterceptors(interceptors);

		return restTemplate;
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

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder(@Value("${spring.jackson.date-format:}") String dateFormat) {
		Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
				.indentOutput(true)
				.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		if (dateFormat==null || dateFormat.isBlank()) {
			builder.dateFormat(new ISODateFormat());
		} else {
			builder.simpleDateFormat(dateFormat);
		}
		return builder;
	}


	@Bean
	public Jsonb jsonb(
			@Value("${spring.jackson.date-format:yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][X]}") String dateFormat) {
		JsonbConfig config=new JsonbConfig();
		config.setProperty(JsonbConfig.FORMATTING, true);
		config.withDateFormat(dateFormat, Locale.getDefault());
		return JsonbBuilder.create(config);
	}
}

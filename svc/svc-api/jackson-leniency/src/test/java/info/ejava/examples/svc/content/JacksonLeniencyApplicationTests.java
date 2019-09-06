package info.ejava.examples.svc.content;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.json.bind.Jsonb;
import java.net.URI;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
class JacksonLeniencyApplicationTests {
	@Autowired
	private RestTemplate restTemplate;
	@Value("${spring.jackson.date-format:yyyy-MM-dd'T'HH:mm:ss.SSSSSS[X]}")
	private String dateFormat;
	@Autowired
	private Jsonb jsonb;

	String baseUrl;

	@BeforeEach
	public void init(@LocalServerPort int port) {
		baseUrl = String.format("http://localhost:%d", port);
	}

	@Test
	void echo() {
		URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("api/dates").build().toUri();
		ADate dates = ADate.of(ZonedDateTime.now(ZoneId.of("UTC")));
		ADate result = restTemplate.postForObject(url, dates, ADate.class);
		assertThat(result).isEqualTo(dates);
	}

	@Test
	void echoString() {
		URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("api/dates/string").build().toUri();
		ADate dates = ADate.of(ZonedDateTime.now(ZoneId.of("UTC")));
		String result = restTemplate.postForObject(url, dates, String.class);

		if (!dateFormat.isEmpty()) {
			Matcher matcher = Pattern.compile(".*\"date\" +: +\"(.+?)\".*", Pattern.DOTALL).matcher(result);
			assertThat(matcher.matches()).isTrue();
			String wireFormat = matcher.group(1);

			Date dateFromWire = Date.from(
					LocalDateTime.parse(wireFormat, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(ZoneOffset.UTC)
			);
			assertThat(dateFromWire).isEqualTo(dates.getDate());
		}
	}

	@Test
	void echoEst2Utc() {
		URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("api/dates/est2utc").build().toUri();
		ADate dates = ADate.of(ZonedDateTime.now(ZoneId.of("EST", ZoneId.SHORT_IDS)));
		ADate result = restTemplate.postForObject(url, dates, ADate.class);

		assertThat(result.getZdt()).isEqualTo(dates.getZdt());
		assertThat(result.getLdt()).isNotEqualTo(dates.getLdt());
		assertThat(result.getInstant()).isEqualTo(dates.getInstant());
		assertThat(result.getDate()).isEqualTo(dates.getDate());

		assertThat(result.getInstant().equals(dates.getInstant())).isTrue();
		assertThat(result).isEqualTo(dates);
	}

	@Test
	public void jsonb2jackson() throws ParseException {
		URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("api/dates").build().toUri();
		
		ADate dates = ADate.of(ZonedDateTime.now(ZoneId.of("UTC"))).truncateDateToMillis();

		//marshal the payload using JSON-B
		String json = jsonb.toJson(dates);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		//we should get an equivalent result
		ADate result = restTemplate.postForObject(url, entity, ADate.class);
		assertThat(result.getDate().getTime()).isEqualTo(dates.getDate().getTime());
		assertThat(result).isEqualTo(dates);
	}

	@Test
	public void jsonb2jacksonEst2Utc() {
		URI url = UriComponentsBuilder.fromHttpUrl(baseUrl).path("api/dates/est2utc").build().toUri();
		ADate dates = ADate.of(ZonedDateTime.now(ZoneId.of("EST", ZoneId.SHORT_IDS)));

		//marshal the payload using JSON-B
		String json = jsonb.toJson(dates);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<String>(json, headers);

		//we should get an equivalent result
		ADate result = restTemplate.postForObject(url, entity, ADate.class);

		assertThat(result.getZdt()).isEqualTo(dates.getZdt());
		assertThat(result.getLdt()).isNotEqualTo(dates.getLdt());
		assertThat(result.getInstant()).isEqualTo(dates.getInstant());
		assertThat(result.getDate()).isEqualTo(dates.getDate());

		assertThat(result.getInstant().equals(dates.getInstant())).isTrue();
		assertThat(result).isEqualTo(dates);
	}
}


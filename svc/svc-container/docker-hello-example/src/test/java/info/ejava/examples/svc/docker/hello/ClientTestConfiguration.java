package info.ejava.examples.svc.docker.hello;

import info.ejava.examples.common.web.RestTemplateConfig;
import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * A test configuration used by remote IT test clients.
 */
@SpringBootConfiguration()
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration       //needed to setup logging
@Slf4j
public class ClientTestConfiguration {
    @Value("${spring.security.user.user}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    @ConfigurationProperties(prefix = "it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    @Bean
    public URI baseUrl(ServerConfig serverConfig) {
        URI baseUrl = serverConfig.build().getBaseUrl();
        log.info("baseUrl={}", baseUrl);
        return baseUrl;
    }

    @Bean
    public String authnUsername() { return username; }


    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder,
                                      ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new RestTemplateLoggingFilter())
                .build();
    }

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder,
                                  ClientHttpRequestFactory requestFactory) {
        return builder.requestFactory(
                        //used to read the streams twice
                        ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(
                        new BasicAuthenticationInterceptor(username, password),
                        new RestTemplateLoggingFilter()
                )
                .build();
    }

    @Bean
    public ClientHttpRequestFactory httpsRequestFactory(
            ServerConfig serverConfig,
            SSLContext sslContext) {
        HttpClient httpsClient = HttpClientBuilder.create()
                .setSSLContext(serverConfig.isHttps() ? sslContext : null)
                .build();
        return new HttpComponentsClientHttpRequestFactory(httpsClient);
    }

    @Bean
    public SSLContext sslContext(ServerConfig serverConfig)  {
        try {
            return SSLContextBuilder.create()
                    .setProtocol("TLSv1.2")
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("unable to establish SSL context", ex);
        }
    }

}

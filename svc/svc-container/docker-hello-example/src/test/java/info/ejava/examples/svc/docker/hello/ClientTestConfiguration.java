package info.ejava.examples.svc.docker.hello;

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
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.net.URL;

/**
 * A test configuration used by remote IT test clients.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration
@Slf4j
public class ClientTestConfiguration {
    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    @ConfigurationProperties(prefix = "it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    //use for IT tests
    @Bean
    public URI baseUrl(ServerConfig serverConfig) {
        URI baseUrl = serverConfig.build().getBaseUrl();
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
            URL trustStoreUrl = null;
            if (serverConfig.getTrustStore()!=null) {
                trustStoreUrl = Thread.currentThread()
                        .getContextClassLoader().getResource("/" + serverConfig.getTrustStore());
                if (null==trustStoreUrl) {
                    throw new IllegalStateException("unable to locate truststore:/" + serverConfig.getTrustStore());
                }
            }
            SSLContextBuilder builder = SSLContextBuilder.create()
                    .setProtocol("TLSv1.2");
            if (trustStoreUrl!=null) {
                builder.loadTrustMaterial(trustStoreUrl, serverConfig.getTrustStorePassword());
            }
            return builder.build();
        } catch (Exception ex) {
            throw new IllegalStateException("unable to establish SSL context", ex);
        }
    }

}

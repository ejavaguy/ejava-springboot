package info.ejava.examples.svc.https;

import info.ejava.examples.common.web.RestTemplateLoggingFilter;
import info.ejava.examples.common.web.ServerConfig;
import info.ejava.examples.svc.https.hello.HttpsExampleApp;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Server;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * A test configuration used by remote IT test clients.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration       //needed to setup logging
@Slf4j
public class ClientTestConfiguration {
    @Value("${spring.security.user.name}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    @ConfigurationProperties("it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    @Bean
    public URI authnUrl(ServerConfig serverConfig) {
        URI baseUrl = serverConfig.getBaseUrl();
        log.info("baseUrl={}", baseUrl);
        return UriComponentsBuilder.fromUri(baseUrl).path("/api/authn/hello").build().toUri();
    }

    /*
    TLS configuration based on great/short article by Geoff Bourne
    https://medium.com/@itzgeoff/using-a-custom-trust-store-with-resttemplate-in-spring-boot-77b18f6a5c39
     */
    @Bean
    public ClientHttpRequestFactory httpsRequestFactory(SSLContext sslContext,
            ServerConfig serverConfig) {
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
                trustStoreUrl = HttpsExampleApp.class.getResource("/" + serverConfig.getTrustStore());
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

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder,
                                  ServerConfig serverConfig,
                                  ClientHttpRequestFactory requestFactory) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(requestFactory))
                .interceptors(new BasicAuthenticationInterceptor(username, password),
                        new RestTemplateLoggingFilter())
                .build();
        return restTemplate;
    }
}

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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * A test configuration used by remote IT test clients.
 */
@SpringBootConfiguration()
@EnableConfigurationProperties //used to set it.server properties
@EnableAutoConfiguration       //needed to setup logging
@Slf4j
public class ClientTestConfiguration {
//    @Value("${it.server.trust-store:}")
//    private String trustStore;
//    @Value("${it.server.trust-store-password:}")
//    private char[] trustStorePassword;
    @Value("${spring.security.user.user}")
    private String username;
    @Value("${spring.security.user.password}")
    private String password;

    @Bean
    @ConfigurationProperties("it.server")
    public ServerConfig itServerConfig() {
        return new ServerConfig();
    }

    /*
    TLS configuration based on great/short article by Geoff Bourne
    https://medium.com/@itzgeoff/using-a-custom-trust-store-with-resttemplate-in-spring-boot-77b18f6a5c39
     */
    private SSLContext sslContext()  {
        ServerConfig serverConfig = itServerConfig();
        URL trustStoreUrl = HttpsExampleApp.class.getResource("/" + serverConfig.getTrustStore());
        if (null==trustStoreUrl) {
            throw new IllegalStateException("unable to locate trustore:/" + serverConfig.getTrustStore());
        }
        try {
            return SSLContextBuilder.create()
                    .loadTrustMaterial(trustStoreUrl, serverConfig.getTrustStorePassword())
                    .setProtocol("TLSv1.2")
                    .build();
        } catch (Exception ex) {
            throw new IllegalStateException("unable to establish SSL context", ex);
        }
    }
    private ClientHttpRequestFactory httpsRequestFactory() {
        HttpClient httpsClient = HttpClientBuilder.create()
                .setSSLContext(sslContext())
                .build();
        return new HttpComponentsClientHttpRequestFactory(httpsClient);
    }

    @Bean
    public RestTemplate authnUser(RestTemplateBuilder builder, ServerConfig serverConfig) {
        RestTemplate restTemplate = builder.requestFactory(
                //used to read the streams twice -- so we can use the logging filter below
                ()->new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()))
                .interceptors(new BasicAuthenticationInterceptor(username, password),
                        new RestTemplateLoggingFilter())
                .build();
        if (serverConfig.isHttps()) {
            log.info("enabling SSL requests");
            restTemplate.setRequestFactory(httpsRequestFactory());
        }
        return restTemplate;
    }
}

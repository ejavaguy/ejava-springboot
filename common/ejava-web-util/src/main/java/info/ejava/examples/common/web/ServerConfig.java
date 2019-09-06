package info.ejava.examples.common.web;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.PostConstruct;
import java.net.URI;

/**
 * This class is used to represent a configuration of an external
 * connection that defaults to http://localhost:8080
 */
@Data
@With
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {
    private String scheme;
    private String host;
    private int port;
    private URI baseUrl;
    private String trustStore;
    private char[] trustStorePassword;

    @PostConstruct
    public ServerConfig build() {
        if (baseUrl==null) {
            scheme = scheme == null ? "http" : scheme.toLowerCase();
            host = host == null ? "localhost" : host;
            if (port == 0) {
                port = scheme.equals("http") ? 8080 : 8787;
            }
            baseUrl = UriComponentsBuilder.newInstance()
                    .scheme(scheme)
                    .host(host)
                    .port(port)
                    .build((Object) null);
        }
        scheme = baseUrl.getScheme();
        host = baseUrl.getHost();
        port = baseUrl.getPort();
        return this;
    }

    public boolean isHttps() {
        return "https".equalsIgnoreCase(scheme);
    }
}

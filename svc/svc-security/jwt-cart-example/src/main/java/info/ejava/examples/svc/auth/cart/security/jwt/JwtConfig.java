package info.ejava.examples.svc.auth.cart.security.jwt;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import javax.validation.constraints.NotNull;
import java.nio.charset.Charset;
import java.util.UUID;

@ConfigurationProperties(prefix = "jwt")
@Data
@Slf4j
public class JwtConfig {
    @NotNull
    private String loginUri;
    private String key;
    private String authoritiesKey = "auth";
    private String headerPrefix = "Bearer ";
    private int expirationSecs=60*60*24;

    public String getKey() {
        if (key==null) {
            key=UUID.randomUUID().toString();
            log.info("generated JWT signing key={}",key);
        }
        return key;
    }
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(getKey().getBytes(Charset.forName("UTF-8")));
    }
    public SecretKey getVerifyKey() {
        return getSigningKey();
    }
}

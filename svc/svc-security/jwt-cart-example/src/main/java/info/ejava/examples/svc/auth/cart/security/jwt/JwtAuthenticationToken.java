package info.ejava.examples.svc.auth.cart.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;

/**
 * This class represents an Authentication request by a JWT. It simply
 * wraps the String token to be handled by the AuthenticationProvider.
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {
    private final String token;

    public JwtAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return token;
    }
}

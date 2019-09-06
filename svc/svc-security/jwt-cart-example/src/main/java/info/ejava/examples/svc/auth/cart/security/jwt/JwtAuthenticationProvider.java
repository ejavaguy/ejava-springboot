package info.ejava.examples.svc.auth.cart.security.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * This provider is able to accept JwtAuthenticationToken authentication requests,
 * parse, verify, and return the full authentication result.
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationProvider(JwtConfig jwtConfig) {
        jwtUtil = new JwtUtil(jwtConfig);
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthenticationToken.class.isAssignableFrom(authentication);
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String token = ((JwtAuthenticationToken)authentication).getToken();
            Authentication authResult = jwtUtil.parseToken(token);
            return authResult;
        } catch (JwtException ex) {
            throw new BadCredentialsException(ex.getMessage());
        }
    }
}

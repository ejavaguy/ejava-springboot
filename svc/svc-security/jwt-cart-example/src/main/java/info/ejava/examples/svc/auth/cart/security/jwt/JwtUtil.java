package info.ejava.examples.svc.auth.cart.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JwtUtil {
    private final JwtConfig jwtConfig;

    /**
     * Returns the JWT if provided in the HTTP request.
     */
    public String getToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header==null || !header.startsWith(jwtConfig.getHeaderPrefix())) {
            return null;
        }

        String token = header.substring(jwtConfig.getHeaderPrefix().length());
        return token;
    }

    /**
     * Sets the token in the HTTP response header
     */
    public void setToken(HttpServletResponse response, String token) {
        response.setHeader(HttpHeaders.AUTHORIZATION, jwtConfig.getHeaderPrefix() + token);
    }

    /**
     * Encodes authenticated user in a JWT and returns token value
     */
    public String generateToken(Authentication authenticated) {
        String token = Jwts.builder()
                .setSubject(authenticated.getName())
                .setIssuedAt(new Date())
                .setExpiration(getExpires())
                .claim(jwtConfig.getAuthoritiesKey(), getAuthorities(authenticated))
                .signWith(jwtConfig.getSigningKey())
                .compact();
        return token;
    }

    /**
     * Parses and validates token and returns re-assembled authenticated user
     * @throws JwtException for invalid or expired token
     */
    public Authentication parseToken(String token) throws JwtException {
        Claims body = Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getVerifyKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        User user = new User(body.getSubject(), "", getGrantedAuthorities(body));
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, token, user.getAuthorities());
        return authentication;
    }

    protected List<String> getAuthorities(Authentication authenticated) {
        return authenticated.getAuthorities().stream()
                .map(a->a.getAuthority())
                .collect(Collectors.toList());
    }

    protected List<GrantedAuthority> getGrantedAuthorities(Claims claims) {
        List<String> authorities = (List) claims.get(jwtConfig.getAuthoritiesKey());
        return authorities==null ? Collections.emptyList() :
            authorities.stream()
                    .map(a->new SimpleGrantedAuthority(a))
                    .collect(Collectors.toList());
    }

    protected Date getExpires() {
        Instant expiresInstant = LocalDateTime.now()
                .plus(jwtConfig.getExpirationSecs(), ChronoUnit.SECONDS)
                .atZone(ZoneOffset.systemDefault())
                .toInstant();
        return Date.from(expiresInstant);
    }
}

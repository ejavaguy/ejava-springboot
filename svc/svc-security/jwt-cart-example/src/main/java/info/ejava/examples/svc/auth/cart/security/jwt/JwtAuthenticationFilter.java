package info.ejava.examples.svc.auth.cart.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtConfig jwtConfig, AuthenticationManager authm) {
        super(new AntPathRequestMatcher(jwtConfig.getLoginUri(), "POST"));
        this.jwtUtil = new JwtUtil(jwtConfig);
        setAuthenticationManager(authm);
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        LoginDTO login = getCredentials(request);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                login.getUsername(), login.getPassword());

        Authentication authResult = getAuthenticationManager().authenticate(authRequest);
        return authResult;
    }

    protected LoginDTO getCredentials(HttpServletRequest request) throws AuthenticationException {
        try {
            return new ObjectMapper().readValue(request.getInputStream(), LoginDTO.class);
        } catch (IOException ex) {
            log.info("error parsing loginDTO", ex);
            throw new BadCredentialsException(ex.getMessage());
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String token = jwtUtil.generateToken(authResult);
        log.info("generated token={}", token);
        jwtUtil.setToken(response, token);
    }
}

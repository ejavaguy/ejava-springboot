package info.ejava.examples.svc.auth.cart.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuthenticationEntryPoint failureResponse = new JwtEntryPoint();

    public JwtAuthorizationFilter(JwtConfig jwtConfig, AuthenticationManager authenticationManager) {
        jwtUtil = new JwtUtil(jwtConfig);
        this.authenticationManager = authenticationManager;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtUtil.getToken(request);
        if (token == null) { //continue on without JWS authn/authz
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication authentication = new JwtAuthenticationToken(token);
            Authentication authenticated = authenticationManager.authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
            filterChain.doFilter(request, response); //continue chain to operation
        } catch (AuthenticationException fail) {
            failureResponse.commence(request, response, fail);
            return; //end the chain and return error to caller
        }
    }
}

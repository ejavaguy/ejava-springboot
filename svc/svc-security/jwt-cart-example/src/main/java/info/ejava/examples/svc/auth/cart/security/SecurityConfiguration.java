package info.ejava.examples.svc.auth.cart.security;

import info.ejava.examples.svc.auth.cart.security.jwt.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Arrays;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Configuration
    @Order(0)
    @RequiredArgsConstructor
    @EnableConfigurationProperties(JwtConfig.class)
    public class APIConfiguration extends WebSecurityConfigurerAdapter {
        private final JwtConfig jwtConfig;
        private final UserDetailsService jdbcUserDetailsService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(m->m.antMatchers("/api/**"));
            http.httpBasic(cfg->cfg.disable());
            http.formLogin(cfg->cfg.disable());
            http.headers(cfg->{
                cfg.xssProtection().disable();
                cfg.frameOptions().disable();
            });
            http.csrf(cfg->cfg.disable());
            http.cors();
            http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));


            http.addFilterAt(new JwtAuthenticationFilter(jwtConfig,
                            authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class);
            http.addFilterAfter(new JwtAuthorizationFilter(jwtConfig,
                            authenticationManager()),
                    JwtAuthenticationFilter.class);
            http.exceptionHandling(cfg->cfg.defaultAuthenticationEntryPointFor(
                            new JwtEntryPoint(),
                            new AntPathRequestMatcher("/api/**")));


            http.authorizeRequests(cfg->cfg.antMatchers("/api/login").permitAll());
            http.authorizeRequests(cfg->cfg.antMatchers("/api/whoami").permitAll());
            http.authorizeRequests(cfg->cfg.antMatchers("/api/carts/**").authenticated());
            }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(jdbcUserDetailsService);
            auth.authenticationProvider(new JwtAuthenticationProvider(jwtConfig));
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @Order(500)
    @RequiredArgsConstructor
    public class H2Configuration extends WebSecurityConfigurerAdapter {
        private final AuthenticationManager authenticationManager;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests(cfg->cfg
                    .antMatchers("/login","/logout").permitAll());
            http.authorizeRequests(cfg->cfg
                    .antMatchers("/h2-console/**").authenticated());
            http.csrf(cfg->cfg.ignoringAntMatchers("/h2-console/**"));
            http.headers(cfg->cfg.frameOptions().disable());
            http.formLogin().successForwardUrl("/h2-console");
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.parentAuthenticationManager(authenticationManager);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.applyPermitDefaultValues();
                return config;
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService jdbcUserDetailsService(DataSource userDataSource) {
        JdbcDaoImpl jdbcUds = new JdbcDaoImpl();
        jdbcUds.setDataSource(userDataSource);
        return jdbcUds;
    }


    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(StringUtils.join(Arrays.asList(
                "ROLE_ADMIN > ROLE_CLERK"),System.lineSeparator()));
        return roleHierarchy;
    }
}

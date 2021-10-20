package info.ejava.examples.svc.authz.authorities.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(
        prePostEnabled = true, //@PreAuthorize("hasAuthority('ROLE_ADMIN')"), @PreAuthorize("hasRole('ADMIN')")
        jsr250Enabled = true,  //@RolesAllowed({"ROLE_MANAGER"})
        securedEnabled = true  //@Secured({"ROLE_MEMBER"})
)
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Configuration
    @Order(0)
    @RequiredArgsConstructor
    public class APIConfiguration extends WebSecurityConfigurerAdapter {
        private final UserDetailsService jdbcUserDetailsService;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/content/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic(cfg->cfg.realmName("AuthzExample"));
            http.formLogin(cfg->cfg.disable());
            http.headers(cfg->{
                cfg.xssProtection().disable();
                cfg.frameOptions().disable();
            });
            http.csrf(cfg->cfg.disable());
            http.cors();
            http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/whoami",
                    "/api/authorities/paths/anonymous/**")
                    .permitAll());
            
            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/authorities/paths/admin/**")
                    .hasRole("ADMIN"));
            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/authorities/paths/clerk/**")
                    .hasAnyRole("ADMIN", "CLERK")); //explicit ADMIN not needed with inheritance
            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/authorities/paths/customer/**")
                    .hasAnyRole("CUSTOMER"));
            http.authorizeRequests(cfg->cfg.antMatchers(HttpMethod.GET,
                    "/api/authorities/paths/price")
                    .hasAnyAuthority("PRICE_CHECK", "ROLE_ADMIN", "ROLE_CLERK"));
            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/authorities/paths/nobody/**")
                    .denyAll());
            http.authorizeRequests(cfg->cfg.antMatchers(
                    "/api/authorities/paths/authn/**")
                    .authenticated());

            //these requests are handled by class/method annotations
            http.authorizeRequests(cfg->cfg
                    .antMatchers("/api/authorities/secured/**",
                            "/api/authorities/jsr250/**",
                            "/api/authorities/expressions/**")
                    .permitAll());
            }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(jdbcUserDetailsService);
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
    public AccessDecisionManager accessDecisionManager() {
        return new UnanimousBased(List.of(
                new WebExpressionVoter(),
                new RoleVoter(),
                new AuthenticatedVoter()));
    }

    //needed mid-way thru lecture
    //@Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(StringUtils.join(List.of(
                "ROLE_ADMIN > ROLE_CLERK",
                "ROLE_CLERK > ROLE_CUSTOMER"
        ),System.lineSeparator()));
        return roleHierarchy;
    }
}

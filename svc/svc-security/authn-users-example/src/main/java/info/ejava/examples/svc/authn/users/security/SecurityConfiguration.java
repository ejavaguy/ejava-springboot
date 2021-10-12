package info.ejava.examples.svc.authn.users.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Configuration
    @Order(0)
    @RequiredArgsConstructor
    public class APIConfiguration extends WebSecurityConfigurerAdapter {
        private final UserDetailsService sharedUserDetailsService;
        private final UserDetailsService jdbcUserDetailsService;

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/content/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(m->m.antMatchers("/api/anonymous/**","/api/authn/**"));
            http.authorizeRequests(cfg->cfg.antMatchers("/api/anonymous/**").permitAll());
            http.authorizeRequests(cfg->cfg.anyRequest().authenticated());

            http.httpBasic(cfg->cfg.realmName("AuthConfigExample"));
            http.formLogin(cfg->cfg.disable());
            http.headers(cfg->{
                cfg.xssProtection().disable();
                cfg.frameOptions().disable();
            });
            http.csrf(cfg->cfg.disable());
            http.cors();
            http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();
            auth.inMemoryAuthentication().passwordEncoder(encoder)
                .withUser("user1").password(encoder.encode("password1")).roles()
                .and()
                .withUser("user2").password(encoder.encode("password1")).roles();
            auth.userDetailsService(sharedUserDetailsService);
            auth.userDetailsService(jdbcUserDetailsService);
        }

        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @Configuration
    @Order(1000)
    public class AltConfiguration extends WebSecurityConfigurerAdapter {
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
        //return NoOpPasswordEncoder.getInstance();
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService sharedUserDetailsService(PasswordEncoder encoder) {
        User.UserBuilder builder = User.builder().passwordEncoder(encoder::encode);
        List<UserDetails> users = List.of(
            builder.username("user1").password("password2").roles().build(),
            builder.username("user3").password("password2").roles().build()
        );
        return new InMemoryUserDetailsManager(users);
    }

    @Bean
    public UserDetailsService jdbcUserDetailsService(DataSource userDataSource) {
        //return new JdbcUserDetailsManager(userDataSource); -- for full CRUD
        //for just UserDetailsService query access
        JdbcDaoImpl jdbcUds = new JdbcDaoImpl();
        jdbcUds.setDataSource(userDataSource);
        return jdbcUds;
    }
}

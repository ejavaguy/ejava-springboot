package info.ejava.examples.svc.authn.authcfg.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Configuration
public class SecurityConfiguration {

    @Configuration
    @Order(0)
    public class APIConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring().antMatchers("/content/**");
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests(cfg->cfg.antMatchers("/api/anonymous/**").permitAll());
            http.authorizeRequests(cfg->cfg.anyRequest().authenticated());

            http.httpBasic(cfg->cfg.realmName("AuthConfigExample"));
            http.formLogin(cfg->cfg.disable());
            http.headers(cfg->{
                cfg.xssProtection().disable();
                cfg.frameOptions().disable();
            });
            http.csrf(cfg->cfg.disable());
            //two different CORS configuration techniques
            if (true) {
                //no args -- uses @Bean to allow all
                http.cors();
            } else {
                //cfg arg -- uses supplied function to restrict to acme.com
                http.cors(cfg -> cfg.configurationSource(getCorsConfigurationSource()));
            }
            http.sessionManagement(cfg->cfg.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        }

        public CorsConfigurationSource getCorsConfigurationSource() {
            return new CorsConfigurationSource() {
                @Override
                public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                    CorsConfiguration config = new CorsConfiguration();
//                    config.applyPermitDefaultValues();
//                    config.addAllowedOrigin("null");
                    config.addAllowedOrigin("http://acme.com");
                    config.setAllowedMethods(Arrays.asList("GET","POST"));
                    return config;
                }
            };
        }
    }

    @Configuration
    @Order(1000)
    public class AltConfiguration extends WebSecurityConfigurerAdapter {
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        return new CorsConfigurationSource() {
            @Override
            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                CorsConfiguration config = new CorsConfiguration();
                config.applyPermitDefaultValues();
//                    config.addAllowedOrigin("null");
//                config.addAllowedOrigin("http://acme.com");
//                config.setAllowedMethods(Arrays.asList("GET","POST"));
                return config;
            }
        };
    }
}

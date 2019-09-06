package info.ejava.examples.svc.auth.cart;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@ConditionalOnProperty(name = "test", havingValue = "false", matchIfMissing = true)
public class SwaggerConfiguration {
    @Bean
    public OpenAPI customOpenAPI() {
        SecurityRequirement bearKeyRequirement = new SecurityRequirement().addList("bearer-key");
        return new OpenAPI()
                .addSecurityItem(bearKeyRequirement) //globally assign to all operations
                .components(new Components()
                        .addSecuritySchemes("bearer-key", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }

    @Configuration
    @Order(100)
    @ConditionalOnClass(WebSecurityConfigurerAdapter.class)
    public static class SwaggerSecurity extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(cfg->cfg
                    .antMatchers("/swagger-ui*", "/swagger-ui/**", "/v3/api-docs/**"));
            http.csrf().disable();
            http.authorizeRequests(cfg->cfg.anyRequest().permitAll());
        }
    }
}

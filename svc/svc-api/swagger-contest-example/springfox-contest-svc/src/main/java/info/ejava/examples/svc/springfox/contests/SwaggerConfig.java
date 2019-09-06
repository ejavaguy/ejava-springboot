package info.ejava.examples.svc.springfox.contests;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(name = "test", havingValue = "false", matchIfMissing = true)
public class SwaggerConfig {

    @ConfigurationProperties("api.swagger")
    @Getter
    @Setter
    public static final class SwaggerConfiguration {
        private String title;
        private String description;
        private String version;
        private String regexp = "/api/.*";
        @NestedConfigurationProperty
        private Contact contact = new Contact();

        @Setter
        @Getter
        public static final class Contact {
            private String name;
            private String url;
            private String email;
        }
    }

    @Bean
    public SwaggerConfiguration swaggerConfiguration() {
        return new SwaggerConfiguration();
    }


    @Bean
    public Docket api(SwaggerConfiguration config) {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.regex(config.getRegexp()))
                .build()
                .directModelSubstitute(Duration.class, String.class)
                .useDefaultResponseMessages(false)
                .apiInfo(new ApiInfoBuilder()
                        .title(config.getTitle())
                        .description(config.getDescription())
                        .version(config.getVersion())
                        .contact(new Contact(config.getContact().getName(),
                                config.getContact().getUrl(),
                                config.getContact().getEmail()))
                        .build()
                );
    }
}

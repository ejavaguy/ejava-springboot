package info.ejava.examples.svc.springdoc.contests;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.SpringDocUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public OpenAPI openAPI(SwaggerConfiguration config) {
        OpenAPI openApi = new OpenAPI();

        Info info = new Info();
        info.setTitle(config.getTitle());
        info.setDescription(config.getDescription());
        info.setVersion(config.getVersion());

        Contact contact = new Contact();
        contact.setName(config.getContact().getName());
        contact.setUrl(config.getContact().getUrl());
        contact.setEmail(config.getContact().getEmail());
        info.setContact(contact);

        openApi.setInfo(info);

        return openApi;
    }


    @Bean
    public GroupedOpenApi api(SwaggerConfiguration config) {
        SpringDocUtils.getConfig()
                .replaceWithSchema(Duration.class,
                        new Schema().example("PT120M"))
            ;
        return GroupedOpenApi.builder()
                .group("contests")
                .pathsToMatch("/api/contests/**")
                .build();
    }
}

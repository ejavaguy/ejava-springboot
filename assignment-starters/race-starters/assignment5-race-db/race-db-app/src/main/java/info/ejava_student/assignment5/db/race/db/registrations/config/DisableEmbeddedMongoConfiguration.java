package info.ejava_student.assignment5.db.race.db.registrations.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;

/**
 * disables Flapdoodle when spring.data.mongodb.uri is defined
 */
@Configuration
@ConditionalOnProperty(prefix="spring.data.mongodb",name="uri",matchIfMissing=false)
@EnableAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
public class DisableEmbeddedMongoConfiguration {
}
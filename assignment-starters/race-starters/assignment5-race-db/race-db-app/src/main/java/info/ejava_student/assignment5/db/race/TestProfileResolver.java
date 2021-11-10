package info.ejava_student.assignment5.db.race;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.ActiveProfilesResolver;
import org.springframework.test.context.support.DefaultActiveProfilesResolver;

/**
 * Determines the profile(s) to use when running tests or wherever using @ActiveProfile
 * and Spring Boot properties.
 * Ref: https://www.allprogrammingtutorials.com/tutorials/overriding-active-profile-boot-integration-tests.php
 */
@Slf4j
public class TestProfileResolver implements ActiveProfilesResolver {
    private final String PROFILE_KEY = "spring.profiles.active";
    private final DefaultActiveProfilesResolver defaultResolver = new DefaultActiveProfilesResolver();

    @Override
    public String[] resolve(Class<?> testClass) {
        log.info("resolving active profile for: {}={}", PROFILE_KEY, System.getProperties().getProperty(PROFILE_KEY));

        return System.getProperties().containsKey(PROFILE_KEY) ?
                //return the profiles expressed in the property as an array of strings
                System.getProperty(PROFILE_KEY).split("\\s*,\\s*") :
                //return profile(s) expresssed in the class' annotation
                defaultResolver.resolve(testClass);
    }
}

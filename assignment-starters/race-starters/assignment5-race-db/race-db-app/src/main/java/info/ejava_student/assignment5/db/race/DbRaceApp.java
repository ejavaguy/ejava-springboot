package info.ejava_student.assignment5.db.race;

import info.ejava_student.assignment5.db.race.db.registrations.config.RacerRegistrationDBConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@SpringBootApplication(scanBasePackageClasses = {
        DbRaceApp.class, //scan here
        /*
        SecurityConfiguration.class, //security configuration
        RacerRegistrationSecurityConfiguration.class, //security controller
        RacerRegistrationConfiguration.class, //scan the API solution
        AOPConfiguration.class, //add in some Aspects
         */
        RacerRegistrationDBConfiguration.class
})
@EnableAutoConfiguration
public class DbRaceApp {
    public static void main(String...args) {
        SpringApplication.run(DbRaceApp.class, args);
    }

    /**
     * Allow ADMIN and MGR access H2 console
     */
    @Configuration
    @Order(50)
    public class H2Configuration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.requestMatchers(cfg->cfg.antMatchers("/h2-console/**","/login","/logout"));
            http.authorizeRequests(cfg->cfg.antMatchers("/login","/logout").permitAll());
            http.authorizeRequests(cfg->cfg.antMatchers("/h2-console/**").hasAnyRole("ADMIN","MGR"));

            http.csrf(cfg->cfg.disable());
            http.headers(cfg->cfg.frameOptions().disable());
            http.formLogin().successForwardUrl("/h2-console");
        }
    }
}
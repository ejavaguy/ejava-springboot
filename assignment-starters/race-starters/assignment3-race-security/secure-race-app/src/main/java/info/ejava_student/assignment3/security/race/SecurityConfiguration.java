package info.ejava_student.assignment3.security.race;


import info.ejava.assignments.security.race.security.RaceAccounts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {
    @Configuration
    @Profile("anonymous-access")
    public class PartA1_AnonymousAccess extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);//TODO
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);//TODO
        }
    }

    @Configuration
    @Profile({"authenticated-access", "userdetails"})
    public class Part1B_AuthenticatedAccess extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);//TODO
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);//TODO
        }
    }

    @Primary
    @ConfigurationProperties("race")
    @Profile("userdetails || authorities || authorization")
    @Bean
    public RaceAccounts accounts() {
        return new RaceAccounts();
    }

    @Configuration
    @Profile("userdetails || authorities || authorization")
    public class Part1C_UserDetailsPart {
        @Bean
        public Object passwordEncoder() {
            return null; //TODO
        }

        @Bean
        public Object userDetailsService(PasswordEncoder encoder, RaceAccounts accounts) {
            return null; //TODO
        }
    }


    @Configuration
    @Profile("authorization")
    public class Part2B_Authorization extends WebSecurityConfigurerAdapter {
        @Autowired
        private UserDetailsService userDetailsService;

        @Override
        public void configure(WebSecurity web) throws Exception {
            super.configure(web);//TODO
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            super.configure(http);//TODO
        }

        //@Bean
        public RoleHierarchy roleHierarchy() {
            RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
            //TODO
            return roleHierarchy;
        }
    }

    //OPTIONAL
    @Configuration
    @Profile("noauth")
    public class AllowAll extends WebSecurityConfigurerAdapter {
        @Override
        public void configure(WebSecurity web) throws Exception {
            //CAN DO
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            //CAN DO
        }
    }
}

package info.ejava.assignments.security.race.config;

import info.ejava.assignments.security.race.security.AccountProperties;
import info.ejava.assignments.security.race.security.RaceAccounts;
import info.ejava.examples.common.web.RestTemplateConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@Profile("authorities")
@Slf4j
public class AuthorizationTestHelperConfiguration {
    @Bean
    @ConfigurationProperties("race")
    @ConditionalOnMissingBean
    public RaceAccounts accounts() {
        return new RaceAccounts();
    }

    private AccountProperties getUserAccount(RaceAccounts accounts, int index) {
        if (accounts.getAccounts().size()>=1) {
            List<AccountProperties> userAccounts = accounts.getAccounts().stream()
                    .filter(a->!a.getAuthorities().contains("ROLE_ADMIN"))
                    .filter(a->!a.getAuthorities().contains("ROLE_MGR"))
                    .filter(a->!a.getAuthorities().contains("PROXY"))
                    .collect(Collectors.toList());
            if (userAccounts.size() <= index) {
               throw new IllegalStateException("cannot find user without elevated roles");
            }
            log.info("using account({}) for anAuthUser", userAccounts.get(index));
            return userAccounts.get(index);
        } else {
            throw new IllegalStateException("no user.name/password or accounts specified");
        }
    }
    @Bean
    public AccountProperties anAccount(RaceAccounts accounts) {
        return getUserAccount(accounts, 0);
    }
    @Bean
    public AccountProperties altAccount(RaceAccounts accounts) {
        return getUserAccount(accounts, 1);
    }


    @Bean
    public AccountProperties mgrAccount(RaceAccounts accounts) {
        return findUserWithAuthority(accounts, "ROLE_MGR");
    }

    @Bean
    public AccountProperties adminAccount(RaceAccounts accounts) {
        return findUserWithAuthority(accounts, "ROLE_ADMIN");
    }

    private AccountProperties findUserWithAuthority(RaceAccounts accounts, String authority) {
        if (accounts.getAccounts().size()>=1) {
            AccountProperties account = accounts.getAccounts().stream()
                    .filter(a->a.getAuthorities().contains(authority))
                    .findFirst()
                    .orElseThrow(()->new IllegalStateException("cannot find user authority " + authority));
            log.info("using account({}) for %s authority", account, authority);
            return account;
        } else {
            throw new IllegalStateException("no user.name/password or accounts specified");
        }
    }

    @Bean
    @Qualifier("userMap")
    public Map<String, RestTemplate> authnUsers(RestTemplateBuilder builder, RaceAccounts accounts) {
        Map<String, RestTemplate> authnUsers = new HashMap<>();
        for (AccountProperties account: accounts.getAccounts()) {
            ClientHttpRequestInterceptor authn=
                    new BasicAuthenticationInterceptor(account.getUsername(), account.getPassword());
            authnUsers.put(account.getUsername(), new RestTemplateConfig().restTemplateDebug(builder, authn));
        }
        return authnUsers;
    }

    @Bean
    public RestTemplate anonymousUser(RestTemplateBuilder builder) {
        return new RestTemplateConfig().restTemplateDebug(builder);
    }
    @Bean
    public RestTemplate authnUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties anAccount) {
        return authnUsers.get(anAccount.getUsername());
    }
    @Bean
    public RestTemplate altUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties altAccount) {
        return authnUsers.get(altAccount.getUsername());
    }
    @Bean
    public RestTemplate mgrUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties mgrAccount) {
        return authnUsers.get(mgrAccount.getUsername());
    }
    @Bean
    public RestTemplate adminUser(@Qualifier("userMap") Map<String, RestTemplate> authnUsers,
                                  AccountProperties adminAccount) {
        return authnUsers.get(adminAccount.getUsername());
    }
}

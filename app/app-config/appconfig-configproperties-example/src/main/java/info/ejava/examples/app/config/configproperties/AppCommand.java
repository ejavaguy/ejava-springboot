package info.ejava.examples.app.config.configproperties;

import info.ejava.examples.app.config.configproperties.properties.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AppCommand implements CommandLineRunner {
    @Autowired
    private CarProperties carProperties;
    @Autowired
    private BoatProperties boatProperties;
    @Autowired
    private CompanyProperties companyProperties;
    @Autowired
    private BusinessProperties businessProperties;
    @Autowired
    private CorporationProperties corporationProperties;
    @Autowired
    private RouteProperties routeProperties;

    @Autowired
    private UserProperties userProps;

    @Autowired
    private PersonProperties ownerProps;
    @Autowired
//    @Manager
    @Qualifier("managerProps")
    private PersonProperties manager;

    public void run(String... args) throws Exception {
        System.out.println("carProperties=" + carProperties);
        System.out.println("====");
        System.out.println("boatProperties=" + boatProperties);
        System.out.println("====");
        System.out.println("companyProperties=" + companyProperties);

        System.out.println("====");
        System.out.println("businessProperties=" + businessProperties);

        System.out.println("====");
        System.out.println("corporationProperties=" + corporationProperties);

        System.out.println("====");
        System.out.println("routeProperties=" + routeProperties);

        System.out.println("====");
        System.out.println(userProps);
        System.out.println("user.home=" + userProps.getHome());

        System.out.println("====");
        System.out.println(ownerProps);
        System.out.println(manager);
    }
}

package info.ejava_student.assignment5.db.race.db.registrations.config;

import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationBO;
import info.ejava_student.assignment5.db.race.db.registrations.RacerRegistrationMapper;
import info.ejava_student.assignment5.jpa.race.raceregistrations.*;
import info.ejava_student.assignment5.mongo.race.raceregistrations.MongoAssignmentService;
import info.ejava_student.assignment5.mongo.race.raceregistrations.MongoAssignmentServiceImpl;
import info.ejava_student.assignment5.mongo.race.raceregistrations.MongoRacerRegistrationRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackageClasses = {RacerRegistrationBO.class})
public class RacerRegistrationDBConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RacerRegistrationMapper mapper() {
        return new RacerRegistrationMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public JpaAssignmentService jpaAssignmentService(RacerRegistrationMapper mapper,
                                                     //EntityManager entityManager,
                                                     JpaRacerRegistrationRepository repo) {
        return new JpaAssignmentServiceImpl(mapper/*, entityManager*/, repo);
    }

    @Bean
    @ConditionalOnMissingBean
    public MongoAssignmentService mongoAssignmentService(RacerRegistrationMapper mapper,
                                                         //MongoOperations mongoOps,
                                                         MongoRacerRegistrationRepository repo) {
        return new MongoAssignmentServiceImpl(mapper/*, mongoOps*/, repo);
    }
}

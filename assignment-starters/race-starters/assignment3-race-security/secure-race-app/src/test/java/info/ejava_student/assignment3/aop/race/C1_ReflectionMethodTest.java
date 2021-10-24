package info.ejava_student.assignment3.aop.race;


import info.ejava.assignments.aop.race.NullPropertyAssertion;
import info.ejava.assignments.api.race.client.factories.RaceDTOFactory;
import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.examples.common.exceptions.ClientErrorException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
@DisplayName("Part C1: Reflection Method")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Disabled("TODO")
public class C1_ReflectionMethodTest {
    private static RaceDTOFactory raceDTOFactory = new RaceDTOFactory();
    private static RacerDTOFactory racerDTOFactory = new RacerDTOFactory();
    private static NullPropertyAssertion nullPropertyAssertion = new NullPropertyAssertionImpl();

    static Stream<Arguments> dtos() {
        return Stream.of(
            Arguments.of(new Object(), List.of("id"), true),
            Arguments.of(raceDTOFactory.make(), List.of("id"), true),
            Arguments.of(racerDTOFactory.make(), List.of("id"), true),
            Arguments.of(raceDTOFactory.make(RaceDTOFactory.withId), List.of("id"), false),
            Arguments.of(racerDTOFactory.make(RacerDTOFactory.withId), List.of("id"), false),
            Arguments.of(raceDTOFactory.make(), List.of("id","name"), false),
            Arguments.of(raceDTOFactory.make().withName(null), List.of("id","name"), true)
        );
    }

    @ParameterizedTest
    @MethodSource("dtos")
    void can_identify_missing_nulls(/*given*/Object object, List<String> properties, boolean success) {
        //when
        ClientErrorException.InvalidInputException ex = BDDAssertions.catchThrowableOfType(
                ()-> nullPropertyAssertion.assertNull(object, properties.toArray(new String[0])),
                ClientErrorException.InvalidInputException.class
        );

        //then
        if (success) {
            then(ex).isNull();
        } else {
            then(ex).as("error not detected by reflection method").isNotNull();
            log.info("{}", ex.getMessage());
            then(ex.getMessage()).contains("must be null");
        }
    }
}

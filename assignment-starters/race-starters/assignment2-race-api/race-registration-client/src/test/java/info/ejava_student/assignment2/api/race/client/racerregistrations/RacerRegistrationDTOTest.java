package info.ejava_student.assignment2.api.race.client.racerregistrations;

import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import info.ejava_student.assignment2.api.race.client.raceregistrations.RacerRegistrationDTO;
import info.ejava_student.assignment2.api.race.client.raceregistrations.RacerRegistrationListDTO;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.BDDAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

@Slf4j
public class RacerRegistrationDTOTest {
    private DtoUtil dtoUtil = JsonUtil.instance();

    //provides parameterized inputs
    private static Stream<Arguments> marshal_unmarshal() {
        return Stream.of(
                //this test data will need to be populated
                Arguments.of(new RacerRegistrationDTO()),
                Arguments.of(new RacerRegistrationListDTO())
        );
    }

    @ParameterizedTest
    @MethodSource()
    void marshal_unmarshal(/*given*/Object original) {
        //when
        String payload = dtoUtil.marshal(original);
        log.info("{}", payload);
        Object clone = dtoUtil.unmarshal(payload, original.getClass());
        //then
        BDDAssertions.then(clone).isEqualTo(original);
    }
}

package info.ejava.assignments.api.race.client.racers;

import info.ejava.assignments.api.race.client.factories.RacerDTOFactory;
import info.ejava.examples.common.dto.DtoUtil;
import info.ejava.examples.common.dto.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.assertj.core.api.BDDAssertions.then;

@Slf4j
public class RacersDTOTest {
    private DtoUtil dtoUtil = JsonUtil.instance();
    private static final RacerDTOFactory racerDtoFactory = new RacerDTOFactory();

    private static final RacerDTO racer = RacerDTO.builder()
            .id("123")
            .firstName("joe")
            .lastName("smith")
            .gender(RacerDTO.GENDERS[0])
            .dob(LocalDate.now())
            .build();

    private static Stream<Arguments> marshal_unmarshal() {
        return Stream.of(
                Arguments.of(racer),
                Arguments.of(racerDtoFactory.make()),
                Arguments.of(new RacerListDTO(racerDtoFactory.listBuilder().make(3,3)))
        );
    }

    @ParameterizedTest
    @MethodSource()
    void marshal_unmarshal(Object original) {
        //when
        String payload = dtoUtil.marshal(original);
        log.info("{}", payload);
        Object clone = dtoUtil.unmarshal(payload, original.getClass());
        //then
        then(clone).isEqualTo(original);
    }
}

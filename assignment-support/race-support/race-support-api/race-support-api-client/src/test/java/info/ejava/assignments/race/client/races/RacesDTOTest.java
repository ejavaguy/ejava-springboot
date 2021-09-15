package info.ejava.assignments.race.client.races;

import info.ejava.assignments.race.client.StreetAddressDTO;
import info.ejava.assignments.race.client.factories.RaceDTOFactory;
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
public class RacesDTOTest {
    private DtoUtil dtoUtil = JsonUtil.instance();
    private static final RaceDTOFactory raceDtoFactory = new RaceDTOFactory();

    private static final StreetAddressDTO location = StreetAddressDTO.builder()
            .city("Laurel")
            .state("MD")
            .build();
    private static final RaceDTO race = RaceDTO.builder()
            .id("123")
            .name("main event")
            .eventLength(13.66)
            .eventDate(LocalDate.now())
            .location(location)
            .build();

    private static Stream<Arguments> marshal_unmarshal() {
        return Stream.of(
                Arguments.of(location),
                Arguments.of(race),
                Arguments.of(raceDtoFactory.make()),
                Arguments.of(new RaceListDTO(raceDtoFactory.listBuilder().make(3,3)))
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

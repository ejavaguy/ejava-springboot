package info.ejava.examples.app.testing.testbasics.grader.svc;

import info.ejava.examples.app.testing.testbasics.grading.ClientError;
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderService;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Grader Service Interface Contract")
@Tag("grader")
public class GraderServiceContractTest {
    @Mock
    private GraderService grader;

    @Nested
    public class submit_grade {
        @Test
        public void valid_student_and_grade() throws ClientError {
            //given - setup
            //given(grader.submitGrade(anyString(), anyInt())).willReturn(0);

            //when - action
            int result = grader.submitGrade("jim", 100);

            //then - checks
            org.junit.jupiter.api.Assertions.assertAll(
                    () -> org.junit.Assert.assertEquals("unexpected result", result, 0),
                    () -> org.junit.jupiter.api.Assertions.assertEquals(result, 0, "unexpected result"),
                    () -> assertThat("unexpected result", result, is(0)),
                    () -> org.assertj.core.api.Assertions.assertThat(result).as("unexpected result").isEqualTo(0)
            );
        }

        @Test
        public void missing_student() throws ClientError {
            //given
            given(grader.submitGrade(isNull(), anyInt())).willThrow(new ClientError.BadRequest("student is required"));

            //when
            Exception ex = assertThrows(ClientError.class, () -> grader.submitGrade(null, 100));

            //then
            Assertions.assertAll(
                    () -> Assertions.assertTrue(ex instanceof ClientError.BadRequest, "unexpected exception"),
                    () -> assertThat(ex, Matchers.instanceOf(ClientError.BadRequest.class)),
                    () -> assertThat(ex.getMessage(), containsString("student")),
                    () -> org.assertj.core.api.Assertions.assertThat(ex)
                            .isInstanceOf(ClientError.BadRequest.class)
                            .hasMessage("student is required")
                            .hasMessageContaining("student")
                            .hasNoCause()
            );
        }

        @Test
        public void missing_grade() throws ClientError {
            //given
            given(grader.submitGrade(anyString(), isNull())).willThrow(new ClientError.BadRequest("grade is required"));

            //when
            Exception ex = assertThrows(ClientError.BadRequest.class, () -> grader.submitGrade("jim", null));

            //then
            Assertions.assertAll(
                    () -> assertThat(ex, Matchers.instanceOf(ClientError.BadRequest.class)),
                    () -> assertThat(ex.getMessage(), containsString("grade"))
            );
        }
    }

    @Nested
    public class get_average {
        private final Logger log = LoggerFactory.getLogger(get_average.class);

        @ParameterizedTest
        @MethodSource("info.ejava.examples.app.testing.testbasics.grader.svc.GraderServiceContractTest#averages")
        public void existing_sudent(String student, Double expectedGrade) throws ClientError {
            given(grader.calcGrade(student)).willReturn(expectedGrade);

            //when
            double result = grader.calcGrade(student);
            log.info("student={}, resultGrage={}", student, result);

            //then
            assertThat(String.format("unepected grade for %s", student), result, closeTo(expectedGrade,0.1));
        }

        @Test
        public void unknown_student() throws ClientError {
            //given
            given(grader.calcGrade(anyString())).willThrow(new ClientError.NotFound("student not found"));

            //when
            Exception ex = assertThrows(ClientError.NotFound.class, () -> grader.calcGrade("joejoe"));

            //then
            assertThat(ex.getMessage(), containsString("student"));
            assertThat(ex.getMessage(), containsString("not found"));
        }

        @Test
        public void missing_student() throws ClientError {
            //given
            given(grader.calcGrade(isNull())).willThrow(new ClientError.BadRequest("student is required"));

            //when
            Exception ex = assertThrows(ClientError.BadRequest.class, () -> grader.calcGrade(null));

            //then
            assertThat(ex.getMessage(), matchesRegex("student.+required"));
        }
    }

    static Stream<Arguments> grades() {
        return Stream.of(
                Arguments.of("jim",100),
                Arguments.of("nancy",100),
                Arguments.of("jim",92),
                Arguments.of("jim",95),
                Arguments.of("nancy",90),
                Arguments.of("jill",85)
        );
    }
    static Stream<Arguments> averages() {
        List<String> students = grades()
                .map(a -> (String) a.get()[0])
                .distinct()
                .collect(Collectors.toList());
        return students.stream()
                .map(student->Arguments.of(student, average(student)));
    }

    static double average(String name) {
        return grades()
                .filter(a->a.get()[0].equals(name))
                .mapToDouble(a->(Integer)a.get()[1])
                .average()
                .orElse(0);
    }
}

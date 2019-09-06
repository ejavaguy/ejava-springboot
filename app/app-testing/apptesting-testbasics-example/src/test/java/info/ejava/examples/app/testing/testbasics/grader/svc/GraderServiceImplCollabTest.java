package info.ejava.examples.app.testing.testbasics.grader.svc;

import info.ejava.examples.app.testing.testbasics.grader.dao.StudentGradeFactory;
import info.ejava.examples.app.testing.testbasics.grading.ClientError;
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository;
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Grader Service Implementation Collaboration")
@Tag("grader")
public class GraderServiceImplCollabTest {
    @Mock
    private GraderRepository graderRepoMock;

    @InjectMocks
    private GraderServiceImpl subject;

    private StudentGradeFactory gf = new StudentGradeFactory();

    @ParameterizedTest
    @MethodSource
    void normalize_name(String name, String expectedNormalized) {
        String normalized = new GraderServiceImpl.LowerCaseNormalizer().normalizeStudentName(name);
        assertThat("unexpected normalization", normalized, is(expectedNormalized));
    }

    static Stream<Arguments> normalize_name() {
        return Stream.of(
                Arguments.of("Jim", "jim"),
                Arguments.of("Jo Jo", "jo jo"));
    }



    @Nested
    class registering_grade {
        //given
        private final String student="Jim";
        private final int grade = 100;

        @BeforeEach
        void when() throws ClientError {
            subject.submitGrade(student, grade);
        }

        @Test
        public void store_in_repository() throws ClientError {
            then(graderRepoMock).should().save(any());
        }

        @Test
        public void name_normalized() {
            ArgumentCaptor<StudentGrade> gradeArgs = ArgumentCaptor.forClass(StudentGrade.class);
            String normalizedName = student.trim().toLowerCase();

            //then - collaboration
            //save grade called
            then(graderRepoMock).should().save(gradeArgs.capture());
            //with normalized name
            assertThat(gradeArgs.getValue().getStudent(), is(normalizedName));
        }
    }

    @Nested
    public class calculating_grades {

        @Captor
        ArgumentCaptor<String> stringArgs;

        @Test
        public void normalizes_name () throws ClientError {
            //given
            String name = "Jo Jo";
            String normalizedName = name.trim().toLowerCase();

            //when
            double result = subject.calcGrade(name);

            // then -- collaborations
            //findAll should be called
            then(graderRepoMock).should().findAll(stringArgs.capture());
            //name should be normalized
            assertThat(stringArgs.getValue(), is(normalizedName));
        }

        @Test
        public void returns_0_for_unknown_user () throws ClientError {
            //when
            double result = subject.calcGrade("unknown");
            //then -- collaborations

            // then -- result
            assertThat(result, is(0.0));
        }

        @Test
        public void known_user () throws ClientError {
            given(graderRepoMock.findAll(anyString())).willReturn(gf.makeStudentGrades("jim",100, 100));
            //when
            double result = subject.calcGrade("jim");

            // then -- collaborations
            verify(graderRepoMock).findAll("jim");

            // then -- result
            assertThat(result, Matchers.not(0));
        }
    }
}

package info.ejava.examples.app.testing.testbasics.grader.svc;

import info.ejava.examples.app.testing.testbasics.grading.ClientError;
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository;
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderService;
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@SpringBootTest(classes={GraderServiceImpl.class})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Grader Service Spring Component Test")
@Tag("springboot") @Tag("grader")
public class GraderServiceImplNTest {
    //@Mock - replaced by
    @MockBean
    private GraderRepository graderRepositoryMock;

    //@InjectMocks - replaced by
    @Autowired
    private GraderService subject;

    @Test
    public void submit_calc_grade() throws ClientError {
        //given
        List<StudentGrade> studentGrades = Arrays.asList(100, 90).stream()
                .map(v -> new StudentGrade("jim",v))
                .collect(Collectors.toList());
        given(graderRepositoryMock.findAll(anyString())).willReturn(studentGrades);

        //when
        int status1 = subject.submitGrade("jim", 100);
        int status2 = subject.submitGrade("jim", 90);
        double result = subject.calcGrade("jim");

        //then
        Assertions.assertAll(
                () -> assertThat(status1+status2, is(0)),
                () -> assertThat(result, closeTo(95.0, 0.1))
        );
    }
}
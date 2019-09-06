package info.ejava.examples.app.testing.spock.grader.svc

import info.ejava.examples.app.testing.testbasics.grading.ClientError
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderService
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderServiceImpl
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification
import spock.lang.Title

import java.util.stream.Collectors

import static spock.util.matcher.HamcrestMatchers.closeTo

@SpringBootTest(classes=[GraderServiceImpl.class])
@Title("Grader Service Spring Component Test")
class GraderServiceImplNSpec extends Specification {
    @SpringBean
    private GraderRepository graderRepositoryMock = Mock();

    @Autowired
    private GraderService subject;

    def "submit calc grade"() throws ClientError {
        given:
            List<StudentGrade> studentGrades = Arrays.asList(100, 90).stream()
                    .map(v -> new StudentGrade("jim",v))
                    .collect(Collectors.toList());
            graderRepositoryMock.findAll(_) >> { studentGrades }

        when:
            int status1 = subject.submitGrade("jim", 100);
            int status2 = subject.submitGrade("jim", 90);
            double result = subject.calcGrade("jim");

        then:
            verifyAll {
                status1+status2 == 0
                result closeTo(95.0, 0.1)
            }
    }
}

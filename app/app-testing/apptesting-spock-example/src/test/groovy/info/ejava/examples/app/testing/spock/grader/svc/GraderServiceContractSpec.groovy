package info.ejava.examples.app.testing.spock.grader.svc

import groovy.util.logging.Slf4j
import info.ejava.examples.app.testing.testbasics.grading.ClientError
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderService
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import java.util.stream.Stream

import static org.hamcrest.Matchers.instanceOf
import static spock.util.matcher.HamcrestMatchers.closeTo

@Slf4j
@Title("Grader Service Interface Contract")
class GraderServiceContractSpec extends Specification {
    GraderService grader = Mock();

    def "submit grade with valid student and grade"() throws ClientError {
        //given - setup

        when: "action"
            int result = grader.submitGrade("jim", 100);

        then: "checks"
            1 * grader.submitGrade(_, _) >> 0
            result == 0
    }

    def "submit grade missing student"() {
        given:
            grader.submitGrade(null, _) >> { throw new ClientError.BadRequest("student is required") }

        when: "when submitting a null student"
            grader.submitGrade(null, 100)

        then:
            Exception expectedException = thrown(ClientError.BadRequest)
            verifyAll(expectedException) {
                message == "student is required"
                message.contains("student")
                cause == null
            }
    }

    def "submitted grade missing grade"() {
        given:
            grader.submitGrade(_ as String, null) >> { throw new ClientError.BadRequest("grade is required") }

        when: "submitting a null grade"
            grader.submitGrade("jim", null)

        then:
            Exception expectedException = thrown(ClientError.BadRequest.class)
            verifyAll(expectedException) {
                instanceOf(ClientError.BadRequest.class)
                message.contains("grade")

            }
    }

    @Unroll
    def "get average for existing student[#student, #expectedGrade]"(String student, Double expectedGrade) {
        given:
            grader.calcGrade(student) >> expectedGrade;

        when:
            double result = grader.calcGrade(student);
            log.info("student={}, resultGrage={}", student, result);

        then:
            result closeTo(expectedGrade,0.1)

        where:
            [student, expectedGrade] << grades()
//            student << grades().map(a->a[0])
//            expectedGrade << grades().map(a->a[1])
    }

    def "get average for unknown student"() {
        given:
            grader.calcGrade(_) >> { throw new ClientError.NotFound("student not found") }

        when:
            grader.calcGrade("joejoe")

        then:
            Exception ex = thrown(ClientError.NotFound)
            verifyAll(ex, Exception) {
                message.contains("student")
                message.contains("not found")
            }
    }

    def "get average for missing student"() throws ClientError {
        given:
            grader.calcGrade(null) >> { throw new ClientError.BadRequest("student is required") }

        when:
            grader.calcGrade(null)

        then:
            Exception ex = thrown(ClientError.BadRequest)
            ex.message?.matches("student.+required")
    }

    static Stream<Tuple> averages() {
        Stream<String> students = grades()
                .map(a -> a[0])
                .distinct();
        return students
                .map(student-> [student, average(student)]);
    }

    static double average(String name) {
        return grades()
                .filter(a->a[0].equals(name))
                .mapToDouble(a->a[1])
                .average()
                .orElse(0);
    }
    static Tuple[] grades() {
        return new Tuple[]{
                ["jim",100],
                ["nancy",100],
                ["jim",92],
                ["jim",95],
                ["nancy",90],
                ["jill",85]
        };
    }
}

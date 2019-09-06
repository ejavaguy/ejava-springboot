package info.ejava.examples.app.testing.spock.grader.svc

import groovy.util.logging.Slf4j
import info.ejava.examples.app.testing.spock.grader.dao.StudentGradeFactory
import info.ejava.examples.app.testing.testbasics.grading.ClientError
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository
import info.ejava.examples.app.testing.testbasics.grading.svc.GraderServiceImpl
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

import static spock.util.matcher.HamcrestMatchers.closeTo

@Slf4j
@Title("Grader Service Implementation Collaboration")
class GraderServiceImplCollabSpec extends Specification {
    GraderRepository graderRepoMock = Mock();
    GraderServiceImpl subject;

    String student="Jim"
    int grade = 100

    private StudentGradeFactory gf = new StudentGradeFactory();

    def setup() {
        subject = new GraderServiceImpl(graderRepoMock)
    }

    @Unroll
    def "normalize name [#name, #expectedNormalized]"(String name, String expectedNormalized) {
        given:
            def normalizer = new GraderServiceImpl.LowerCaseNormalizer()
        when:
            def normalized = normalizer.normalizeStudentName(name)
        then:
            normalized == expectedNormalized
        where:
            name    || expectedNormalized
            "Jim"   || "jim"
            "Jo Jo" || "jo jo"
    }

    def "store grade in repository"() {
        when:
            subject.submitGrade(student, grade)
        then:
            1 * graderRepoMock.save(_)
    }

    def "store grade with name normalized"() {
        String normalizedName = student.trim().toLowerCase();

        when:
            subject.submitGrade(student, grade)

        then:
            interaction {
                1 * graderRepoMock.save({it.student == normalizedName })
            }
    }

    def "calculating grade uses normalized name"() {
        given:
            String name = "Jo Jo";
            String normalizedName = name.trim().toLowerCase();

        when:
            double result = subject.calcGrade(name);

        then: "findAll should be called with normalized name"
            graderRepoMock.findAll(_ as String) >> { String key ->
                assert key == normalizedName  //evaluate key is normalized
                return Collections.emptyList()
            }
    }

    def void "calculating grade returns 0 for unknown user"() {
        given:
            graderRepoMock.findAll(_) >> { Collections.emptyList() }
        when:
            double result = subject.calcGrade("unknown")
        then: "collaborations"
            result closeTo(0.0, 0.0)
    }

    def "register grade for known user"() throws ClientError {
        when:
            double result = subject.calcGrade("jim");

        then: "collaborations"
            //a find method will be called one time and return a value for grades
            1 * graderRepoMock.findAll("jim") >> { gf.makeStudentGrades("jim",100, 100) }
        and:
            result != 0
    }
}

package info.ejava.examples.app.testing.spock.grader.dao

import info.ejava.examples.app.testing.testbasics.grading.ServerError
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository
import spock.lang.Specification
import spock.lang.Title

import java.util.stream.IntStream

import static org.hamcrest.Matchers.hasSize

@Title("Grader Repository Interface Contract")
class GraderRepositoryContractSpec extends Specification {
    private GraderRepository repository = Mock();
    private StudentGradeFactory gf = new StudentGradeFactory();

    def "add grade"() {
        given:
            StudentGrade studentGrade = new StudentGrade("jim", 100);
        when:
            repository.save(studentGrade);
        then:
            noExceptionThrown()
    }

    def "get grades no grades"() {
        when:
            List<StudentGrade> grades = repository.findAll("jim");
        then:
            1 * repository.findAll(_) >> Arrays.asList();
            grades hasSize(0);
    }

    def "get grades one grade"() {
        when:
            List<StudentGrade> grades = repository.findAll("jim");
        then:
            1 * repository.findAll(_ as String) >> { _ -> gf.makeStudentGrades("jim", 100) }
            grades hasSize(1);
    }

    def "get grades many grades"() {
        given:
            Integer[] values = IntStream.range(0, 100).mapToObj(i->i).toArray(Integer[]::new);
        when:
            List<StudentGrade> grades = repository.findAll("jim");
        then:
            1 * repository.findAll(_) >> { _ -> gf.makeStudentGrades("jim", values) }
            grades hasSize(values.length);
    }

    def "get grades fails"() {
        given:
            repository.findAll(_) >> { _ -> throw new ServerError.InternalFailure("something happened") }
        when:
            repository.findAll("jim");
        then:
            Exception ex = thrown(ServerError.InternalFailure);
            ex.getMessage() contains("something");
    }
}

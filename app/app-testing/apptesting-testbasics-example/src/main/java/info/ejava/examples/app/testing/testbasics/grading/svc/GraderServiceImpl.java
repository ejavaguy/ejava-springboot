package info.ejava.examples.app.testing.testbasics.grading.svc;

import info.ejava.examples.app.testing.testbasics.grading.ClientError;
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GraderServiceImpl implements GraderService {
    private final GraderRepository graderRepository;
    private NameNormalizer nameNormalizer=new LowerCaseNormalizer();

    public GraderServiceImpl(GraderRepository graderRepository) {
        this.graderRepository = graderRepository;
    }

    public interface NameNormalizer {
        String normalizeStudentName(String name);
    }

    public static class LowerCaseNormalizer implements NameNormalizer {
        public String normalizeStudentName(String name) {
            return name==null ? null : name.trim().toLowerCase();
        }
    }


    @Override
    public int submitGrade(String student, Integer grade) throws ClientError {
        //validate input
        if (student==null) {
            throw new ClientError.BadRequest("student is required");
        } else if (grade == null) {
            throw new ClientError.BadRequest("grade is required");
        }

        //normalize input
        student = nameNormalizer.normalizeStudentName(student);

        //store the grade
        StudentGrade studentGrade =  new StudentGrade(student, grade);
        graderRepository.save(studentGrade);
        return 0;
    }

    @Override
    public double calcGrade(String student) throws ClientError {
        //validate input
        if (student==null) {
            throw new ClientError.BadRequest("student is required");
        }

        //normalize input
        student = nameNormalizer.normalizeStudentName(student);

        //obtain the grades
        List<StudentGrade> grades = graderRepository.findAll(student);

        //calculate average
        double result= 0;
        if (!grades.isEmpty()) {
            double sum = grades.stream().mapToInt(sg->sg.getGrade()).sum();
            result = sum/grades.size();
        }

        return result;
    }
}

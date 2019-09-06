package info.ejava.examples.app.testing.testbasics.grading.bo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentGrade {
    private String student;
    private int grade;
}

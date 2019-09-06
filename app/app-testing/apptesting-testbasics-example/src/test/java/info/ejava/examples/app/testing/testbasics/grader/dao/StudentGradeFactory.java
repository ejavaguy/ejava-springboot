package info.ejava.examples.app.testing.testbasics.grader.dao;

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StudentGradeFactory {

    public List<StudentGrade> makeStudentGrades(final String student, Integer...grades) {
        return Arrays.stream(grades)
                .map(g->new StudentGrade(student, g))
                .collect(Collectors.toList());
    }
}

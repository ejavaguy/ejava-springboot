package info.ejava.examples.app.testing.spock.grader.dao

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade

import java.util.stream.Collectors

class StudentGradeFactory {
    public List<StudentGrade> makeStudentGrades(final String student, Integer...grades) {
        return Arrays.stream(grades)
                .map(g->new StudentGrade(student, g))
                .collect(Collectors.toList())
    }
}

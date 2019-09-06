package info.ejava.examples.app.testing.testbasics.grading.dao;

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GraderRepositoryImpl implements GraderRepository {
    Map<String, List<StudentGrade>> grades = new HashMap<>();

    @Override
    public void save(StudentGrade studentGrade) {
        List<StudentGrade> studentGrades = grades.get(studentGrade.getStudent());
        if (studentGrades==null) {
            studentGrades=new ArrayList<>();
            grades.put(studentGrade.getStudent(), studentGrades);
        }
        studentGrades.add(studentGrade);
    }

    @Override
    public List<StudentGrade> findAll(String student) {
        List<StudentGrade> studentGrades = grades.get(student);
        return studentGrades==null ? Collections.emptyList() : studentGrades;
    }
}

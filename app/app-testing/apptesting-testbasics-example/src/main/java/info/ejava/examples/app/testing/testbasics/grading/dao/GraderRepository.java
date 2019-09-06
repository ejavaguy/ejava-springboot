package info.ejava.examples.app.testing.testbasics.grading.dao;

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;

import java.util.List;

public interface GraderRepository {
    public void save(StudentGrade studentGrade);
    public List<StudentGrade> findAll(String student);
}

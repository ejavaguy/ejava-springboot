package info.ejava.examples.app.testing.testbasics.grading.svc;

import info.ejava.examples.app.testing.testbasics.grading.ClientError;

public interface GraderService {
    int submitGrade(String student, Integer grade) throws ClientError;
    double calcGrade(String student) throws ClientError;
}

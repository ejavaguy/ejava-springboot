package info.ejava.examples.app.testing.testbasics.grader.dao;


import info.ejava.examples.app.testing.testbasics.grading.ServerError;
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import info.ejava.examples.app.testing.testbasics.grading.dao.GraderRepository;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("Grader Repository Interface Contract")
@Tag("grader")
public class GraderRepositoryContractTest {
    @Mock
    private GraderRepository repository;
    private StudentGradeFactory gf = new StudentGradeFactory();

    @Test
    public void add_grade() {
        StudentGrade studentGrade = new StudentGrade("jim", 100);
        //when

        repository.save(studentGrade);
    }

    @Nested
    public class get_grades {

        @Test
        public void no_grades() {
            //given
            given(repository.findAll(anyString())).willReturn(Arrays.asList());
            //when
            List<StudentGrade> grades = repository.findAll("jim");
            //then
            assertThat(grades, Matchers.hasSize(0));
        }

        @Test
        public void one_grade() {
            //given
            given(repository.findAll(anyString())).willReturn(gf.makeStudentGrades("jim", 100));
            //when
            List<StudentGrade> grades = repository.findAll("jim");
            //then
            assertThat(grades, Matchers.hasSize(1));
        }

        @Test
        public void many_grades() {
            //given
            Integer[] values = IntStream.range(0, 100).mapToObj(i->i).toArray(Integer[]::new);
            given(repository.findAll(anyString())).willReturn(gf.makeStudentGrades("jim", values));
            //when
            List<StudentGrade> grades = repository.findAll("jim");
            //then
            assertThat(grades, Matchers.hasSize(values.length));
        }

        @Test
        public void fails() {
            //given
            given(repository.findAll(anyString())).willThrow(new ServerError.InternalFailure("something happened"));
            //when
            Exception ex = assertThrows(ServerError.InternalFailure.class, () -> repository.findAll("jim"));
            //then
            assertThat(ex.getMessage(), containsString("something"));
        }
    }
}

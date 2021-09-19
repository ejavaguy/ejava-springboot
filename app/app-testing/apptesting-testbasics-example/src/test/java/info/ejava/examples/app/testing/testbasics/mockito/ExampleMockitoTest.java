package info.ejava.examples.app.testing.testbasics.mockito;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.and;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("mocks")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@DisplayName("map")
public class ExampleMockitoTest {
    @Mock //creating a mock to configure for use in each test
    private Map<String, String> mapMock;

    @Captor
    private ArgumentCaptor<String> stringArgCaptor;

    @Test
    public void listMap() {
        //define behavior of mock during test
        when(mapMock.get(stringArgCaptor.capture()))
                .thenReturn("springboot", "testing");

        //conduct test
        int size = mapMock.size();
        String secret1 = mapMock.get("happiness");
        String secret2 = mapMock.get("joy");

        //evaluate results
        verify(mapMock).size(); //verify called once
        verify(mapMock, times(2)).get(anyString()); //verify called twice
        //verify what was given to mock
        assertThat(stringArgCaptor.getAllValues().get(0)).isEqualTo("happiness");
        assertThat(stringArgCaptor.getAllValues().get(1)).isEqualTo("joy");
        //verify what was returned by mock
        assertThat(size).as("unexpected size").isZero();
        assertThat(secret1).as("unexpected first result").isEqualTo("springboot");
        assertThat(secret2).as("unexpected second result").isEqualTo("testing");
    }
    @Test
    public void listMap_no_capture() {
        //define behavior of mock during test
        when(mapMock.get(anyString())) //not capturing input
                .thenReturn("springboot", "testing");

        //conduct test
        int size = mapMock.size();
        String secret1 = mapMock.get("happiness");
        String secret2 = mapMock.get("joy");

        //evaluate results
        verify(mapMock).size(); //verify called once
        verify(mapMock, times(2)).get(anyString()); //verify called twice
        //verify what was returned by mock
        assertThat(size).as("unexpected size").isZero();
        assertThat(secret1).as("unexpected first result").isEqualTo("springboot");
        assertThat(secret2).as("unexpected second result").isEqualTo("testing");
    }

    @Nested @Tag("bdd")
    public class when_has_key {
        @Test
        public void returns_values() {
            //given
            given(mapMock.get(stringArgCaptor.capture()))
                    .willReturn("springboot", "testing");

            //when
            int size = mapMock.size();
            String secret1 = mapMock.get("happiness");
            String secret2 = mapMock.get("joy");

            //then - can use static import for BDDMockito or BDDAssertions, not both
            then(mapMock).should().size(); //verify called once
            then(mapMock).should(times(2)).get(anyString()); //verify called twice

            //and.then requires aspectj-core 3.14.0
            and.then(stringArgCaptor.getAllValues().get(0)).isEqualTo("happiness");
            and.then(stringArgCaptor.getAllValues().get(1)).isEqualTo("joy");
            and.then(size).as("unexpected size").isZero();
            and.then(secret1).as("unexpected first result").isEqualTo("springboot");
            and.then(secret2).as("unexpected second result").isEqualTo("testing");
        }
    }
}


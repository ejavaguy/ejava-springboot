package info.ejava.examples.app.testing.testbasics.jupiter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class ExampleJUnit5Test {
    @BeforeAll
    static void setUpClass() {
        log.info("setUpClass");
    }
    @BeforeEach
    void setUp() {
        log.info("setUp");
    }
    @AfterEach
    void tearDown() {
        log.info("tearDown");
    }
    @AfterAll
    static void tearDownClass() {
        log.info("tearDownClass");
    }

    @Test
    void two_plus_two() {
        log.info("2+2=4");
        assertEquals(4,2+2);
        Exception ex=assertThrows(IllegalArgumentException.class, () ->{
            throw new IllegalArgumentException("just demonstrating an expected exception");
        });
        assertTrue(ex.getMessage().startsWith("just demo"));
    }
    @Test
    void one_and_one() {
        log.info("1+1=2");
        assertTrue(1+1==2, "problem with 1+1");
        assertTrue(1+1==2, ()->String.format("problem with %d+%d",1,1));
    }

    @Test
    public void exceptions() {
        RuntimeException ex1 = Assertions.assertThrows(RuntimeException.class,
            () -> {
                throw new IllegalArgumentException("example exception");
            });
    }

}

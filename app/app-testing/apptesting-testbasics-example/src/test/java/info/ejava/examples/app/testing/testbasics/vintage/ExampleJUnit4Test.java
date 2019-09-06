package info.ejava.examples.app.testing.testbasics.vintage;

import lombok.extern.slf4j.Slf4j;
import org.junit.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@Slf4j
public class ExampleJUnit4Test {
    @BeforeClass
    public static void setUpClass() {
        log.info("setUpClass");
    }
    @Before
    public void setUp() {
        log.info("setUp");
    }
    @After
    public void tearDown() {
        log.info("tearDown");
    }
    @AfterClass
    public static void tearDownClass() {
        log.info("tearDownClass");
    }

    @Test(expected = IllegalArgumentException.class)
    public void two_plus_two() {
        log.info("2+2=4");
        assertEquals(4,2+2);
        throw new IllegalArgumentException("just demonstrating an expected exception");
    }
    @Test
    public void one_and_one() {
        log.info("1+1=2");
        assertTrue("problem with 1+1", 1+1==2);
        assertTrue(String.format("problem with %d+%d",1,1), 1+1==2);
    }
}

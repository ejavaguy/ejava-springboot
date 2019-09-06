package info.ejava.examples.app.testing.spock

import groovy.util.logging.Slf4j
import spock.lang.PendingFeature
import spock.lang.Specification

@Slf4j
class ExampleSpockSpec extends Specification {
    //fields
    //fixture methods
    def setupSpec() {
        log.info("setupSpec")
    }
    def setup() {
        log.info("setup")
    }
    def cleanup() {
        log.info("cleanup")
    }
    def cleanupSpec() {
        log.info("cleanupSpec")
    }
    //feature methods
    //helper methods

    def "two plus two"() {
        log.info("2+2=4");
        expect:
            4 == 2+2;
    }

    def "one and one"() {
        log.info("1+1=2");
        when:
            def one=1
        then:
            verifyAll {
                2 == one + one
                assert 2 == one + one: "problem with 1+1"
            }
    }

    def "exceptions"() {
        when:
            throw new IllegalArgumentException("example exception")

        then:
            RuntimeException ex1 = thrown(RuntimeException)
    }

    @PendingFeature //this will not execute
    def "an upcoming requirement"(){
        given: "an empty list"
            def list
        when: "we ask for its size"
            size = list.size()
        then: "it will return 0"
            size == 0
    }
}


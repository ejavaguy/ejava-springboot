package info.ejava.examples.app.testing.spock

import groovy.util.logging.Slf4j
import spock.lang.Specification

/**
 * Two types of interactions. We can have both performed at once but must abide by
 * placement rules when doing that.
 * <ol>
 *     <li>mock interactions - worry if, when, how, and/or how many times called.
 *        evaluation expression placed in the when block</li>
 *     <li>stub interactions - worry about responses to stimulus.
 *        response expression can be in the given, when, or then block. must be in
 *        then block when combined with mock evaluation definition</li>
 * </ol>
 */
@Slf4j
class ExampleMockSpec extends Specification {
    Map mapMock = Mock()

    def "list map stub"() {
        given: "a stub interaction defined in given block"
            mapMock = Stub() //we can optionally used just a stub in this case
            mapMock.get(_) >>> ["springboot", "testing"] //can be relocated to when or then block
            mapMock = Stub() {  //can define declaration if just a stub
                get(_) >>> ["springboot", "testing"] //can be relocated to when or then block
            }

        when: "calling the mock" //can have many when blocks followed by then blocks
            int size = mapMock.size()
            String secret1 = mapMock.get("foo")
            String secret2 = mapMock.get("bar")
            log.info("secret1={}, secret2={}", secret1, secret2)

        then: "produces the values defined in the stub declaration"
            size == 0
            secret1 == "springboot"
            secret2 == "testing"
        and: "but we cannot evaluate whether the stub methods were called"
    }

     def "list map mock interactions"() {
         given: "mock interactions declared in then block"
         when: "calling the mock"
             int size = mapMock.size()
             def value1 = mapMock.get("happiness")
             def value2 = mapMock.get("joy")
             log.info("value1={}, value2={}", value1, value2)

         then: "we can evaluate whether mock called"
             1 * mapMock.size() //this is an interaction definition
             2 * mapMock.get(_) //scoped with preceding when block
         and: "but pure mock will not have a return value"
             value1 == null
             value2 == null
     }

    def "list map mock interactions and stub results"() {
        given: "mock interaction and stub result defined in then"
        when: "calling the mock"
            def answer1 = mapMock.get("one")
            def answer2 = mapMock.get("a")
            log.info("answer1={}, answer2={}", answer1, answer2)

        then: "we MUST evaluate mock interactions where we define stub response"
            //mocks can be used as mocks and stubs
            with(mapMock) { //these specs are not expressing order
                1 * get("a") >> "b"     //defines both mock interactions
                1 * get("one") >> "two"  //   and stub responses
            }
        and: "verify stubs returned configured values"
            answer1 == "two"
            answer2 == "b"
    }

    def "multiple when/then blocks"() {
        when: "having multiple when/then blocks"
            def result1 = mapMock.get("icecream")
            log.info("result1={}", result1)
        then: "the then is scoped to preceding when"
            1 * mapMock.get("icecream") >> "cold"
        result1 == "cold"

        when: "subsequent when blocks"
            def result2 = mapMock.get("pie")
            log.info("result2={}", result2)
        then: "can be evaluated separately"
            1 * mapMock.get("pie") >> "warm"
            result2 == "warm"
    }

    def "interaction order"() {
        given: "we want to dictate a particular order"
        when: "we call in certain order"
            mapMock.get("one")
            mapMock.get("two")
        //these separate then blocks are expressing call order
        then: "place first interaction spec in an initial then block"
            1*mapMock.get("one")
        then: "place second interaction spec in follow-on then block"
            1*mapMock.get("two")

    }
}

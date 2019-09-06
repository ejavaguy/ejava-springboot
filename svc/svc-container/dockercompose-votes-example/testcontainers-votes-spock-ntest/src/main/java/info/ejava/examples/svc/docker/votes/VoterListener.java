package info.ejava.examples.svc.docker.votes;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class VoterListener {
    @Getter
    private AtomicInteger msgCount=new AtomicInteger(0);

    @PostConstruct
    public void init() {
        log.info("here");
    }

    @JmsListener(destination = "votes")
    public void receive(TextMessage msg) throws JMSException {
        log.info("jmsMsg={}, {}", msgCount.incrementAndGet(), msg.getText());
    }
}

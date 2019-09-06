package info.ejava.examples.svc.docker.votes.events;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.ejava.examples.svc.docker.votes.dto.VoteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class VoterJMS {
    private final JmsTemplate jmsTemplate;
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper mapper;

    public void publish(VoteDTO vote) throws JsonProcessingException {
        final String json = mapper.writeValueAsString(vote);

        jmsTemplate.send("votes", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createTextMessage(json);
            }
        });
    }

    @JmsListener(destination = "votes")
    public void receive(TextMessage message) throws JMSException {
        String json = message.getText();
        try {
            VoteDTO vote = mapper.readValue(json, VoteDTO.class);
            log.info("new vote receieved {}", vote);
            eventPublisher.publishEvent(new NewVoteEvent(vote));
        } catch (JsonProcessingException ex) {
            log.warn("error parsing vote {}", json, ex);
        }
    }
}

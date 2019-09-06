package info.ejava.examples.svc.springmvc.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="message", namespace="urn:ejava.svc.todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO implements Serializable {
    private String text;

    @Override
    public String toString() {
        return text;
    }
}

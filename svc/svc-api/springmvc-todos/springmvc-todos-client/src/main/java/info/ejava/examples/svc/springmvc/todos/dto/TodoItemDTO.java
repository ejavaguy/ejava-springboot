package info.ejava.examples.svc.springmvc.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name="todoItem", namespace="urn:ejava.svc.todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoItemDTO implements Serializable {
    private String name;
    private Integer priority;
}

package info.ejava.examples.svc.springmvc.todos.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name="todoList", namespace="urn:ejava.svc.todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListDTO implements Serializable {
    private String name;
    private List<TodoItemDTO> todoItems;

    @JsonIgnore
    public TodoItemDTO getListItem(String itemName) {
        if (todoItems==null) { return null; }
        return todoItems.stream()
                        .filter(item->itemName.equalsIgnoreCase(item.getName()))
                        .findFirst()
                        .orElseGet(null);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("TodoList[name=").append(name)
               .append(", todoItems=");
        if (todoItems!=null) {
            boolean first=true;
            for (TodoItemDTO todo: todoItems) {
                if (!first) { builder.append(",").append(System.lineSeparator()); }
                builder.append(todo);
                first=false;
            }
        }
        builder.append("]");
        return builder.toString();
    }
}
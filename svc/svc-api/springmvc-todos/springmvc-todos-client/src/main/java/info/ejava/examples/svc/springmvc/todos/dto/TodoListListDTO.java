package info.ejava.examples.svc.springmvc.todos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement(name="todoListList", namespace="urn:ejava.svc.todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoListListDTO implements Serializable {
   private List<TodoListDTO> todoLists;

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("TodoList[todos=");
      if (todoLists!=null) {
         boolean first=true;
         for (TodoListDTO todoList: todoLists) {
            if (!first) { builder.append(",").append(System.lineSeparator()); }
            builder.append(todoList);
            first=false;
         }
      }
      builder.append("]");
      return builder.toString();
   }

   public int getCount() {
      return todoLists==null ? 0 : todoLists.size();
   }
   public void setCount(int count) { //nothing to set
   }

}

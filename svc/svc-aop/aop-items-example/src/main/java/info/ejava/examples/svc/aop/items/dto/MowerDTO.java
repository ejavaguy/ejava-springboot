package info.ejava.examples.svc.aop.items.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class MowerDTO extends ItemDTO {
    @Builder(builderMethodName = "mowerBuilder")
    public MowerDTO(int id, String name) {
        super(id, name);
    }
}

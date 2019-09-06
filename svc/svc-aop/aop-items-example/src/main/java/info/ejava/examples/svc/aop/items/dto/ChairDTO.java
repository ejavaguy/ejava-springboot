package info.ejava.examples.svc.aop.items.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class ChairDTO extends ItemDTO {
    @Builder(builderMethodName = "chairBuilder")
    public ChairDTO(int id, String name) {
        super(id, name);
    }
}

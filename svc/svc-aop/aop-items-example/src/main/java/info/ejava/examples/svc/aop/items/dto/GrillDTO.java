package info.ejava.examples.svc.aop.items.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class GrillDTO extends ItemDTO {
    @Builder(builderMethodName = "grillBuilder")
    public GrillDTO(int id, String name) {
        super(id, name);
    }
}

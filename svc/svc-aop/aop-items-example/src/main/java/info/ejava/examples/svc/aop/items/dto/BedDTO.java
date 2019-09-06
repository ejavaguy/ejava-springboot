package info.ejava.examples.svc.aop.items.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@ToString(callSuper = true)
public class BedDTO extends ItemDTO {
    @Builder(builderMethodName = "bedBuilder")
    public BedDTO(int id, String name) {
        super(id, name);
    }
}

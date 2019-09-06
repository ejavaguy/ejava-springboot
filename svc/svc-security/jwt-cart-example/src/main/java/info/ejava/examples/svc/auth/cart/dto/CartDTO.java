package info.ejava.examples.svc.auth.cart.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class CartDTO {
    private String username;
    private List<String> items = new ArrayList<>();

    public CartDTO(String username) {
        this.username = username;
    }
}

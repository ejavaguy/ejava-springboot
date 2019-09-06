package info.ejava.examples.svc.auth.cart.security.jwt;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
    private String username;
    private String password;
}

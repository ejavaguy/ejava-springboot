package info.ejava.examples.svc.auth.cart.services;

import info.ejava.examples.svc.auth.cart.dto.CartDTO;
import org.springframework.security.access.prepost.PreAuthorize;

public interface CartsService {

    @PreAuthorize("#username == authentication.name and hasRole('CUSTOMER')")
    CartDTO createCart(String username);

    @PreAuthorize("#username == authentication.name or hasRole('CLERK')")
    CartDTO getCart(String username);

    @PreAuthorize("#username == authentication.name")
    CartDTO addItem(String username, String item);

    @PreAuthorize("#username == authentication.name or hasRole('ADMIN')")
    boolean removeCart(String username);
}

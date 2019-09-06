package info.ejava.examples.svc.auth.cart.controllers;

import info.ejava.examples.svc.auth.cart.dto.CartDTO;
import info.ejava.examples.svc.auth.cart.services.CartsService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class CartsController {
    private final CartsService cartsService;

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    //@Operation(security = { @SecurityRequirement(name = "bearer-key") })
    public ResponseEntity<CartDTO> createCart(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails user) {
        CartDTO cart = cartsService.createCart(user.getUsername());

        ResponseEntity<CartDTO> response = ResponseEntity.ok(cart);
        return response;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CartDTO> getCart(
            @RequestParam(name="username", required = false) String username,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails user) {
        if (username==null) {
            username=user.getUsername();
        }
        CartDTO cart = cartsService.getCart(username);

        ResponseEntity<CartDTO> response = ResponseEntity.ok(cart);
        return response;
    }

    @PostMapping("items")
    public ResponseEntity<CartDTO> addItem(
            @RequestParam(name="username", required=false) String username,
            @RequestParam(name="name", required = true) String item,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails user) {
        if (username==null) {
            username=user.getUsername();
        }
        CartDTO cart = cartsService.addItem(username, item);

        ResponseEntity<CartDTO> response = ResponseEntity.ok(cart);
        return response;
    }


    @DeleteMapping
    public ResponseEntity<CartDTO> removeCart(
            @RequestParam(name="username", required = false) String username,
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails user) {
        if (username==null) {
            username=user.getUsername();
        }
        boolean found = cartsService.removeCart(username);

        ResponseEntity<CartDTO> response = found ?
                ResponseEntity.ok().build() : ResponseEntity.noContent().build();
        return response;
    }
}

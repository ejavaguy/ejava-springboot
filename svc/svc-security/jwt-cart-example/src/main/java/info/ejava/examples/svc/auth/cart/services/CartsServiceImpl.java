package info.ejava.examples.svc.auth.cart.services;

import info.ejava.examples.common.exceptions.ClientErrorException;
import info.ejava.examples.svc.auth.cart.dto.CartDTO;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CartsServiceImpl implements CartsService {
    private ConcurrentMap<String, CartDTO> carts = new ConcurrentHashMap<>();

    @Override
    public CartDTO createCart(String username) {
        validateUsername(username);

        CartDTO cart = carts.get(username);
        if (cart==null) {
            cart = new CartDTO(username);
            carts.put(username, cart);
        }

        return cart;
    }

    @Override
    public CartDTO getCart(String username) {
        CartDTO cart = carts.get(username);
        if (cart==null) {
            throw new ClientErrorException.NotFoundException("no cart found for %s",username);
        }
        return cart;
    }

    @Override
    public CartDTO addItem(String username, String item) {
        validateItem(item);

        CartDTO cart = getCart(username);
        cart.getItems().add(item);

        return cart;
    }

    @Override
    public boolean removeCart(String username) {
        CartDTO cart = carts.remove(username);
        return cart!=null;
    }

    private void validateUsername(String username) {
        if (username==null || username.isBlank()) {
            throw new ClientErrorException.InvalidInputException("invalid null or blank username");
        }
    }
    private void validateItem(String item) {
        if (item==null || item.isBlank()) {
            throw new ClientErrorException.InvalidInputException("invalid null or blank item");
        }
    }
}

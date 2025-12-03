package app.shopCart.service;

import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository) {

        this.cartItemRepository = cartItemRepository;
    }

    @Transactional
    public void removeItemFromShopCartById(UUID itemId, ShopCart shopCart) {

        cartItemRepository.findById(itemId)
                .filter(item -> item.getShopCart().getId().equals(shopCart.getId()))
                .ifPresent(item -> {
                    item.setShopCart(null);
                    shopCart.getItems().remove(item);
                    cartItemRepository.delete(item);
                });
    }

    public void saveToRepo(CartItem item) {

        cartItemRepository.save(item);
    }
}
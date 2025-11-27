package app.shopCart.service;

import app.product.model.Product;
import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.repository.CartItemRepository;
import app.shopCart.repository.ShopCartRepository;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ShopCartService {

    private final ShopCartRepository shopCartRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public ShopCartService(ShopCartRepository shopCartRepository,
                           CartItemRepository cartItemRepository) {

        this.shopCartRepository = shopCartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public ShopCart createInitShopCart(User user) {

        ShopCart shopCart = new ShopCart();
        shopCart.setOwner(user);
        shopCart.setItems(new ArrayList<>());

        shopCartRepository.save(shopCart);
        return shopCart;
    }

    public ShopCart getShopCartByUserOwnerId(UUID userId) {

        return shopCartRepository.getByOwnerId(userId);
    }

    public int getTotalItemsCount(ShopCart shopCart) {

        return shopCart.getItems()
                .stream()
                .mapToInt(CartItem::getCount)
                .sum();
    }

    public BigDecimal getTotalItemsPrice(ShopCart shopCart) {

        return shopCart.getItems()
                .stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getCount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addProductToShopCart(Product product, UUID userId) {

        ShopCart shopCart = getShopCartByUserOwnerId(userId);

        CartItem currentCartItem = shopCart.getItems()
                .stream()
                .filter(item -> item.getName().equals(product.getName()))
                .findFirst()
                .orElse(null);

        if (currentCartItem != null) {
            currentCartItem.setCount(currentCartItem.getCount() + 1);

            cartItemRepository.save(currentCartItem);
            shopCart.setItems(List.of(currentCartItem));
        } else {
            CartItem newCartItem = CartItem.builder()
                    .name(product.getName())
                    .price(product.getPrice())
                    .gender(product.getGender().toString().toLowerCase())
                    .image(product.getImage())
                    .count(1)
                    .createdOn(LocalDateTime.now())
                    .shopCart(shopCart)
                    .build();

            cartItemRepository.save(newCartItem);
            shopCart.setItems(List.of(newCartItem));
        }
    }

    public void removeItemFromShopCart(UUID itemId, UUID userId) {

        ShopCart shopCart = getShopCartByUserOwnerId(userId);

        cartItemRepository.findById(itemId)
                .filter(item -> item.getShopCart().getId().equals(shopCart.getId()))
                .ifPresent(cartItemRepository::delete);
    }
}
package app.shopCart.service;

import app.product.model.Product;
import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.repository.ShopCartRepository;
import app.user.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class ShopCartService {

    private final ShopCartRepository shopCartRepository;
    private final CartItemService cartItemService;

    @Autowired
    public ShopCartService(ShopCartRepository shopCartRepository,
                           CartItemService cartItemService) {

        this.shopCartRepository = shopCartRepository;
        this.cartItemService = cartItemService;
    }

    public ShopCart createInitShopCart(User user) {

        ShopCart shopCart = new ShopCart();
        shopCart.setOwner(user);
        shopCart.setItems(new ArrayList<>());

        shopCartRepository.save(shopCart);
        log.info("---New empty shopping cart was created in the system for user [%s].".formatted(user.getUsername()));

        return shopCart;
    }

    public ShopCart getShopCartByUserOwnerId(UUID userId) {

        return shopCartRepository.getByOwnerId(userId);
    }

    public List<ShopCart> getAllEmptyShopCarts() {

        return shopCartRepository.findAll()
                .stream()
                .filter(c -> !c.getItems().isEmpty())
                .toList();
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

            cartItemService.saveToRepo(currentCartItem);
            shopCart.setItems(List.of(currentCartItem));

            log.info("---The product [%s] was increment by 1 for user [%s]."
                    .formatted(currentCartItem.getName(), shopCart.getOwner().getUsername()));
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

            cartItemService.saveToRepo(newCartItem);
            shopCart.setItems(List.of(newCartItem));

            log.info("---The product [%s] was added in the shopping cart for user [%s]."
                    .formatted(newCartItem.getName(), shopCart.getOwner().getUsername()));
        }
    }

    @Transactional
    public void cleanShopCartByUserId(UUID userId) {

        ShopCart shopCart = getShopCartByUserOwnerId(userId);

        shopCart.getItems().clear();

        log.info("---Shopping cart for user [%s] was cleared."
                .formatted(shopCart.getOwner().getUsername()));
    }
}
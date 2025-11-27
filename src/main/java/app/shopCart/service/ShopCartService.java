package app.shopCart.service;

import app.shopCart.model.ShopCart;
import app.shopCart.repository.ShopCartRepository;
import app.user.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ShopCartService {

    private final ShopCartRepository shopCartRepository;

    @Autowired
    public ShopCartService(ShopCartRepository shopCartRepository) {
        this.shopCartRepository = shopCartRepository;
    }

    public ShopCart createInitShopCart(User user) {

        ShopCart shopCart = new ShopCart();
        shopCart.setOwner(user);
        shopCart.setItems(new ArrayList<>());

        shopCartRepository.save(shopCart);
        return shopCart;
    }
}
package app.web;

import app.product.model.Product;
import app.product.service.ProductService;
import app.security.UserData;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("/shop-cart")
public class ShopCartController {

    private final ShopCartService shopCartService;
    private final ProductService productService;
    private final UserService userService;

    @Autowired
    public ShopCartController(ShopCartService shopCartService,
                              ProductService productService,
                              UserService userService) {

        this.shopCartService = shopCartService;
        this.productService = productService;
        this.userService = userService;
    }

    @PostMapping("/{productId}/add-to-cart")
    public String addToShoppingCart(@PathVariable UUID productId,
                                           @AuthenticationPrincipal UserData userData) {

        Product product = productService.getById(productId);
        User user = userService.getById(userData.getUserId());

        shopCartService.addProductToShopCart(product, user);

        return "redirect:/products";
    }
}

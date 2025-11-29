package app.web;

import app.product.model.Product;
import app.product.service.ProductService;
import app.security.UserData;
import app.shopCart.model.ShopCart;
import app.shopCart.service.CartItemService;
import app.shopCart.service.ShopCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
import java.util.UUID;

@Controller
@RequestMapping("/shop-cart")
public class ShopCartController {

    private final ShopCartService shopCartService;
    private final CartItemService cartItemService;
    private final ProductService productService;

    @Autowired
    public ShopCartController(ShopCartService shopCartService,
                              CartItemService cartItemService,
                              ProductService productService) {

        this.shopCartService = shopCartService;
        this.cartItemService = cartItemService;
        this.productService = productService;
    }

    @GetMapping
    public ModelAndView getShopCartPage(@AuthenticationPrincipal UserData userData) {

        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(userData.getUserId());
        int itemsTotalSum = shopCartService.getTotalItemsCount(shopCart);
        BigDecimal itemsTotalPrice = shopCartService.getTotalItemsPrice(shopCart);

        ModelAndView model = new ModelAndView("shop-cart");
        model.addObject("items", shopCart.getItems());
        model.addObject("itemsTotalSum", itemsTotalSum);
        model.addObject("itemsTotalPrice", itemsTotalPrice);

        return model;
    }

    @PostMapping("/{productId}/add-to-cart")
    public String addToShoppingCart(@PathVariable UUID productId,
                                    @AuthenticationPrincipal UserData userData) {

        Product product = productService.getById(productId);

        shopCartService.addProductToShopCart(product, userData.getUserId());

        return "redirect:/products";
    }

    @DeleteMapping("/{itemId}/remove-from-cart")
    public String removeItemFromCart(@PathVariable UUID itemId,
                                     @AuthenticationPrincipal UserData userData) {

        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(userData.getUserId());

        cartItemService.removeItemFromShopCartById(itemId, shopCart);

        return "redirect:/shop-cart";
    }
}
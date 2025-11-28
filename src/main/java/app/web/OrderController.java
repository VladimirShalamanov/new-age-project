package app.web;

import app.order.service.OrderService;
import app.security.UserData;
import app.shopCart.model.ShopCart;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.UserUtils;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;

@Controller
@RequestMapping("/order")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final ShopCartService shopCartService;

    public OrderController(OrderService orderService,
                           UserService userService,
                           ShopCartService shopCartService) {

        this.orderService = orderService;
        this.userService = userService;
        this.shopCartService = shopCartService;
    }

    @GetMapping("/payment")
    public ModelAndView getPaymentPage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());
        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(userData.getUserId());

        int itemsTotalSum = shopCartService.getTotalItemsCount(shopCart);
        BigDecimal itemsTotalPrice = shopCartService.getTotalItemsPrice(shopCart);

        boolean hasFullName = UserUtils.hasFullName(user);
        boolean hasFullAddress = UserUtils.hasFullAddress(user);

        String fullNames = hasFullName ? UserUtils.buildFullName(user) : "Add first and last name";
        String fullAddress = hasFullAddress ? UserUtils.buildFullAddress(user) : "Add city and address";

        boolean hasProducts = itemsTotalSum > 0;
        boolean missingProfileInfo = !(hasFullName && hasFullAddress);

        ModelAndView model = new ModelAndView("payment");

        model.addObject("user", user);
        model.addObject("itemsTotalSum", itemsTotalSum);
        model.addObject("itemsTotalPrice", itemsTotalPrice);
        model.addObject("fullNames", fullNames);
        model.addObject("fullAddress", fullAddress);
        model.addObject("missingProfileInfo", missingProfileInfo);
        model.addObject("hasProducts", hasProducts);
        model.addObject("canCreateOrder", hasProducts && !missingProfileInfo);

        return model;
    }

    @PostMapping("/payment")
    public String createOrder(@AuthenticationPrincipal UserData userData) {

        orderService.createOrderByUserId(userData.getUserId());

        shopCartService.cleanShopCartByUserId(userData.getUserId());

        return "redirect:/";
    }
}
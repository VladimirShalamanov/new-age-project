package app;

import app.order.model.Order;
import app.order.service.OrderService;
import app.product.model.Product;
import app.product.model.ProductGender;
import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.service.CartItemService;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.EditUserProfileRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class OrderPaymentITest {

    @Autowired
    private UserService userService;

    @Autowired
    private ShopCartService shopCartService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Test
    void buyOneProduct() {

        RegisterRequest registerRequest = new RegisterRequest("testVlad",
                "1212", "1212", "vlad@abv.bg");
        userService.register(registerRequest);

        EditUserProfileRequest dto = EditUserProfileRequest.builder()
                .firstName("Vladimir")
                .lastName("Shalamanov")
                .city("Sofia")
                .address("ul. Vladimir Shalamanov 38")
                .build();
        UUID userId = userService.getByUsername("testVlad").getId();
        userService.updateUserProfile(userId, dto);
        User registeredUser = userService.getByUsername("testVlad");

        Product product = aRandomProduct();
        shopCartService.addProductToShopCart(product, registeredUser.getId());
        orderService.createOrderByUserId(registeredUser.getId());
        shopCartService.cleanShopCartByUserId(registeredUser.getId());

        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(registeredUser.getId());
        List<Order> allUserOrders = orderService.getAllOrdersByOwnerId(registeredUser.getId());
        Order order = allUserOrders.stream().findFirst().get();

        assertThat(shopCart.getItems()).hasSize(0);
        assertThat(allUserOrders).hasSize(1);
        assertNotNull(allUserOrders);
        assertEquals(new BigDecimal("25.00"), order.getTotalPrice());
        assertEquals(1, order.getTotalCount());
    }

    @Test
    void buyTwoProduct() {

        RegisterRequest registerRequest = new RegisterRequest("testVlad",
                "1212", "1212", "vlad@abv.bg");
        userService.register(registerRequest);

        EditUserProfileRequest dto = EditUserProfileRequest.builder()
                .firstName("Vladimir")
                .lastName("Shalamanov")
                .city("Sofia")
                .address("ul. Vladimir Shalamanov 38")
                .build();
        UUID userId = userService.getByUsername("testVlad").getId();
        userService.updateUserProfile(userId, dto);
        User registeredUser = userService.getByUsername("testVlad");

        Product product = aRandomProduct();
        shopCartService.addProductToShopCart(product, registeredUser.getId());
        shopCartService.addProductToShopCart(product, registeredUser.getId());
        orderService.createOrderByUserId(registeredUser.getId());
        shopCartService.cleanShopCartByUserId(registeredUser.getId());

        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(registeredUser.getId());
        List<Order> allUserOrders = orderService.getAllOrdersByOwnerId(registeredUser.getId());
        Order order = allUserOrders.stream().findFirst().get();

        assertThat(shopCart.getItems()).hasSize(0);
        assertThat(allUserOrders).hasSize(1);
        assertNotNull(allUserOrders);
        assertEquals(new BigDecimal("50.00"), order.getTotalPrice());
        assertEquals(2, order.getTotalCount());
    }

    @Test
    void removeProductFromShopCart() {

        RegisterRequest registerRequest = new RegisterRequest("testVlad",
                "1212", "1212", "vlad@abv.bg");
        userService.register(registerRequest);
        User registeredUser = userService.getByUsername("testVlad");

        Product product = aRandomProduct();
        shopCartService.addProductToShopCart(product, registeredUser.getId());

        ShopCart shopCart = shopCartService.getShopCartByUserOwnerId(registeredUser.getId());
        CartItem cartItem = shopCart.getItems().stream().findFirst().get();
        cartItemService.removeItemFromShopCartById(cartItem.getId(), shopCart);

        ShopCart updatedShopCart = shopCartService.getShopCartByUserOwnerId(registeredUser.getId());

        assertThat(updatedShopCart.getItems()).hasSize(0);
    }

    public static Product aRandomProduct() {

        return Product.builder()
                .name("prod")
                .price(new BigDecimal("25.00"))
                .gender(ProductGender.MALE)
                .description("some init test desc")
                .image("/images/testImg")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
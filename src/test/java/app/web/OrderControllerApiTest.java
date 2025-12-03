package app.web;

import app.order.service.OrderService;
import app.product.model.ProductGender;
import app.security.UserData;
import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    OrderService orderService;
    @MockitoBean
    ShopCartService shopCartService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getPaymentPage_shouldReturnPaymentViewWithCorrectModel() throws Exception {

        User user = aRandomUser();

        when(userService.getById(any())).thenReturn(user);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getPermissions(), user.isActive());

        ShopCart shopCart = user.getShopCart();
        CartItem item1 = aRandomCartItem(shopCart);
        CartItem item2 = aRandomCartItem(shopCart);

        shopCart.getItems().add(item1);
        shopCart.getItems().add(item2);

        when(userService.getById(user.getId())).thenReturn(user);
        when(shopCartService.getShopCartByUserOwnerId(user.getId())).thenReturn(shopCart);

        MockHttpServletRequestBuilder httpRequest = get("/orders/payment")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("payment"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("itemsTotalSum", 2))
                .andExpect(model().attribute("itemsTotalPrice", new BigDecimal("50.00")))
                .andExpect(model().attribute("fullNames", "Vladimir Shalamanov"))
                .andExpect(model().attribute("fullAddress", "Sofia, ul Berlin 33"))
                .andExpect(model().attribute("missingProfileInfo", false))
                .andExpect(model().attribute("hasProducts", true))
                .andExpect(model().attribute("canCreateOrder", true));
    }

    public User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("12test")
                .password("1212")
                .email("test@abv.bg")
                .role(UserRole.USER)
                .permissions(Set.of())
                .active(true)
                .firstName("Vladimir")
                .lastName("Shalamanov")
                .city("Sofia")
                .address("ul Berlin 33")
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ShopCart shopCart = new ShopCart();
        shopCart.setOwner(user);
        shopCart.setItems(new ArrayList<>());

        user.setShopCart(shopCart);

        return user;
    }

    public static CartItem aRandomCartItem(ShopCart shopCart) {

        return CartItem.builder()
                .id(UUID.randomUUID())
                .name("prod")
                .price(new BigDecimal("25.00"))
                .gender(ProductGender.MALE.toString())
                .image("/images/testImg")
                .count(1)
                .createdOn(LocalDateTime.now())
                .shopCart(shopCart)
                .build();
    }
}
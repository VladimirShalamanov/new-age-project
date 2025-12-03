package app.web;

import app.product.model.Product;
import app.product.model.ProductGender;
import app.product.service.ProductService;
import app.security.UserData;
import app.shopCart.model.CartItem;
import app.shopCart.model.ShopCart;
import app.shopCart.service.CartItemService;
import app.shopCart.service.ShopCartService;
import app.user.model.User;
import app.user.model.UserRole;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ShopCartController.class)
public class ShopCartControllerApiTest {

    @MockitoBean
    ShopCartService shopCartService;
    @MockitoBean
    ProductService productService;
    @MockitoBean
    CartItemService cartItemService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getShopCartPage_shouldReturnShopCartViewWithCorrectModel() throws Exception {

        User user = aRandomUser();
        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getPermissions(), user.isActive());

        CartItem cartItem = aRandomCartItem(user.getShopCart());
        user.getShopCart().getItems().add(cartItem);

        when(shopCartService.getShopCartByUserOwnerId(user.getId())).thenReturn(user.getShopCart());

        MockHttpServletRequestBuilder httpRequest = get("/shop-cart")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("shop-cart"))
                .andExpect(model().attribute("items", user.getShopCart().getItems()))
                .andExpect(model().attribute("itemsTotalSum", 1))
                .andExpect(model().attribute("itemsTotalPrice", cartItem.getPrice()));
    }

    @Test
    void addToShoppingCart_shouldRedirectToProductsAndInvokeService() throws Exception {

        User user = aRandomUser();
        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getPermissions(), user.isActive());

        Product product = Product.builder()
                .id(UUID.randomUUID())
                .name("Test Product")
                .price(new BigDecimal("25.00"))
                .build();

        when(productService.getById(product.getId())).thenReturn(product);

        MockHttpServletRequestBuilder httpRequest = post("/shop-cart/{productId}", product.getId())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        verify(shopCartService).addProductToShopCart(product, user.getId());
    }

    @Test
    void removeItemFromCart_shouldRedirectToShopCartAndInvokeService() throws Exception {

        User user = aRandomUser();
        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getPermissions(), user.isActive());

        UUID itemId = UUID.randomUUID();

        when(shopCartService.getShopCartByUserOwnerId(user.getId())).thenReturn(user.getShopCart());

        MockHttpServletRequestBuilder httpRequest = delete("/shop-cart/{itemId}", itemId)
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop-cart"));

        verify(cartItemService).removeItemFromShopCartById(itemId, user.getShopCart());
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
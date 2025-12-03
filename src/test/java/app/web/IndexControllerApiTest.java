package app.web;

import app.security.UserData;
import app.shopCart.model.ShopCart;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.service.UserService;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(IndexController.class)
public class IndexControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    private UserProperties userProperties;

    @Autowired
    private MockMvc mockMvc;

    @Captor
    private ArgumentCaptor<RegisterRequest> registerRequestArgumentCaptor;

    @Test
    void getIndexEndpoint_shouldReturn200okAndIndexView() throws Exception {

        MockHttpServletRequestBuilder httpRequest = get("/");

        mockMvc.perform(httpRequest)
                .andExpect(view().name("index"))
                // andExpect() is (200))
                .andExpect(status().isOk());
    }

    @Test
    void postRegister_shouIdReturn302RedirectAndRedirectToLoginAndInvokeRegisterServiceMethod() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "12test")
                .formField("password", "1212")
                .formField("repeatPassword", "1212")
                .formField("email", "test12@gmail.com")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("successfulRegistration"));

        verify(userService).register(registerRequestArgumentCaptor.capture());

        RegisterRequest dto = registerRequestArgumentCaptor.getValue();
        assertEquals("12test", dto.getUsername());
        assertEquals("1212", dto.getPassword());
        assertEquals("1212", dto.getRepeatPassword());
        assertEquals("test12@gmail.com", dto.getEmail());
    }

    @Test
    void postRegisterWithInvalidFormData_shouldReturn2000kAndShowRegisterViewAndRegisterServiceMethodIsNeverInvoked() throws Exception {

        MockHttpServletRequestBuilder httpRequest = post("/register")
                .formField("username", "V")
                .formField("password", "2")
                .formField("repeatPassword", "")
                .formField("email", "test12gmail-com")
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("register"));

        verifyNoInteractions(userService);
    }

    @Test
    void getIndexPage_shouldReturnHomeViewWithUserModelAttributeAndStatusCodeIs200() throws Exception {

        User user = aRandomUser();
        when(userService.getById(any())).thenReturn(user);

        UserData authentication = new UserData(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getRole(), user.getPermissions(), user.isActive());

        MockHttpServletRequestBuilder httpRequest = get("/")
                .with(user(authentication));

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("user"));
    }

    public static User aRandomUser() {

        User user = User.builder()
                .id(UUID.randomUUID())
                .username("12test")
                .password("1212")
                .email("test@abv.bg")
                .role(UserRole.USER)
                .permissions(List.of())
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        ShopCart shopCart = ShopCart.builder()
                .id(UUID.randomUUID())
                .owner(user)
                .items(new ArrayList<>())
                .build();

        user.setShopCart(shopCart);

        return user;
    }
}
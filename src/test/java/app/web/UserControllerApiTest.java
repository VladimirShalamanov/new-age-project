package app.web;

import app.order.service.OrderService;
import app.security.UserData;
import app.user.model.User;
import app.user.model.UserPermissions;
import app.user.model.UserRole;
import app.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerApiTest {

    @MockitoBean
    private UserService userService;
    @MockitoBean
    OrderService orderService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAdminPanel_whenUserIsAdmin_thenReturnStatus200AndAdminPanelView() throws Exception {

        List<User> users = List.of(aRandomUser(), aRandomUser());
        when(userService.getAllUsers()).thenReturn(users);

        UserDetails authentication = adminAuthentication();
        MockHttpServletRequestBuilder httpRequest = get("/users/admin-panel")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isOk())
                .andExpect(view().name("admin-panel"))
                .andExpect(model().attribute("users", users));
    }

    @Test
    void patchRequestToChangeUserStatus_fromAdminUser_shouIdReturnRedirectAndInvokeServiceMethod() throws Exception {

        UserDetails authentication = adminAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/admin-panel/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/admin-panel"));

        verify(userService).switchStatus(any());
    }

    @Test
    void patchRequestToChangeUserStatus_fromNormaIUser_shouIdReturn404StatusCodeAndViewNotFound() throws Exception {

        UserDetails authentication = userAuthentication();
        MockHttpServletRequestBuilder httpRequest = patch("/users/admin-panel/{userId}/status", UUID.randomUUID())
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(httpRequest)
                .andExpect(status().isNotFound())
                .andExpect(view().name("not-found"));

        verifyNoInteractions(userService);
    }

    public static UserDetails adminAuthentication() {

        return new UserData(UUID.randomUUID(), "22vlad", "1212", "test@abv.bg",
                UserRole.ADMIN, List.of(UserPermissions.ACCOUNTANT), true);
    }

    public static UserDetails userAuthentication() {

        return new UserData(UUID.randomUUID(), "11vlad", "1212", "test@abv.bg",
                UserRole.USER, List.of(), true);
    }

    public static User aRandomUser() {

        // if necessary add shopCart, Orders...
        return User.builder()
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
    }
}
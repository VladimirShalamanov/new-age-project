package app.web;

import app.order.model.Order;
import app.order.service.OrderService;
import app.security.UserData;
import app.user.model.User;
import app.user.model.UserPermissions;
import app.user.model.UserRole;
import app.user.service.UserService;
import app.web.dto.EditUserProfileRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void getUserProfilePage_shouldReturnViewWithUserAndOrders() throws Exception {

        User user = aRandomUser();
        List<Order> orders = List.of(aRandomOrder(), aRandomOrder());

        when(userService.getById(any())).thenReturn(user);
        when(orderService.getAllOrdersByOwnerId(any())).thenReturn(orders);

        UserDetails authentication = userAuthentication();
        MockHttpServletRequestBuilder request = get("/users/user-profile")
                .with(user(authentication))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("orders", orders));
    }

    @Test
    void getEditUserProfilePage_shouldReturnEditViewWithUserAndDto() throws Exception {

        User user = aRandomUser();

        when(userService.getById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder request = get("/users/{id}/profile", user.getId())
                .with(user(adminAuthentication()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit-profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("editUserProfileRequest",
                        Matchers.hasProperty("firstName", Matchers.is(user.getFirstName()))))
                .andExpect(model().attribute("editUserProfileRequest",
                        Matchers.hasProperty("lastName", Matchers.is(user.getLastName()))));
    }

    @Test
    void updateUserProfile_withValidData_shouldRedirectAndInvokeService() throws Exception {

        User user = aRandomUser();

        when(userService.getById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", user.getId())
                .param("firstName", "Vladimir")
                .param("lastName", "Shalamanov")
                .param("city", "New York")
                .param("address", "123 ul tuk sam")
                .with(user(adminAuthentication()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/users/user-profile"));

        ArgumentCaptor<EditUserProfileRequest> captor = ArgumentCaptor.forClass(EditUserProfileRequest.class);
        verify(userService).updateUserProfile(eq(user.getId()), captor.capture());

        EditUserProfileRequest updatedDto = captor.getValue();
        assertEquals("Vladimir", updatedDto.getFirstName());
        assertEquals("Shalamanov", updatedDto.getLastName());
        assertEquals("New York", updatedDto.getCity());
        assertEquals("123 ul tuk sam", updatedDto.getAddress());
    }

    @Test
    void updateUserProfile_withBindingErrors_shouldReturnEditView() throws Exception {

        User user = aRandomUser();

        when(userService.getById(user.getId())).thenReturn(user);

        MockHttpServletRequestBuilder request = put("/users/{id}/profile", user.getId())
                .param("firstName", "")
                .param("lastName", "S")
                .param("city", "New York")
                .param("address", "123 ul tuk sam")
                .with(user(adminAuthentication()))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("user-edit-profile"))
                .andExpect(model().attribute("user", user));

        verify(userService).getById(user.getId());
        verify(userService, never()).updateUserProfile(any(), any());
    }

    public static Order aRandomOrder() {

        return Order.builder()
                .id(UUID.randomUUID())
                .totalCount(1)
                .totalPrice(new BigDecimal("25.00"))
                .build();
    }

    public static UserDetails adminAuthentication() {

        return new UserData(UUID.randomUUID(), "22vlad", "1212", "test@abv.bg",
                UserRole.ADMIN, Set.of(UserPermissions.ACCOUNTANT), true);
    }

    public static UserDetails userAuthentication() {

        return new UserData(UUID.randomUUID(), "11vlad", "1212", "test@abv.bg",
                UserRole.USER, Set.of(), true);
    }

    public static User aRandomUser() {

        return User.builder()
                .id(UUID.randomUUID())
                .username("12test")
                .password("1212")
                .email("test@abv.bg")
                .role(UserRole.USER)
                .permissions(Set.of())
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
    }
}
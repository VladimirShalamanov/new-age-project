package app.web;

import app.order.model.Order;
import app.order.service.OrderService;
import app.security.UserData;
import app.user.model.User;
import app.user.service.UserService;
import app.web.dto.DtoMapper;
import app.web.dto.EditUserProfileRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService,OrderService orderService) {

        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/user-profile")
    public ModelAndView getUserProfilePage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());
        List<Order> orders = orderService.getAllOrdersByOwnerId(userData.getUserId());

        ModelAndView model = new ModelAndView("user-profile");
        model.addObject("user", user);
        model.addObject("orders", orders);

        return model;
    }

    @GetMapping("/{id}/profile")
    public ModelAndView getEditUserProfilePage(@PathVariable UUID id) {

        User user = userService.getById(id);
        EditUserProfileRequest editUserProfileRequest = DtoMapper.fromUser(user);

        ModelAndView model = new ModelAndView("user-edit-profile");
        model.addObject("editUserProfileRequest", editUserProfileRequest);
        model.addObject("user", user);

        return model;
    }

    @PutMapping("/{id}/profile")
    public ModelAndView updateUserProfile(@Valid EditUserProfileRequest editUserProfileRequest,
                                          BindingResult bindingResult,
                                          @PathVariable UUID id) {

        if (bindingResult.hasErrors()) {
            User user = userService.getById(id);

            ModelAndView model = new ModelAndView("user-edit-profile");
            model.addObject("user", user);

            return model;
        }

        userService.updateUserProfile(id, editUserProfileRequest);

        return new ModelAndView("redirect:/users/user-profile");
    }

    @GetMapping("/admin-panel")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAdminPanel() {

        List<User> users = userService.getAll();

        ModelAndView model = new ModelAndView("admin-panel");
        model.addObject("users", users);

        return model;
    }

    @PatchMapping("/admin-panel/{userId}/permission")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserPermissions(@PathVariable UUID userId) {

        userService.switchPermission(userId);

        return "redirect:/users/admin-panel";
    }

    @PatchMapping("/admin-panel/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserRole(@PathVariable UUID userId) {

        userService.switchRole(userId);

        return "redirect:/users/admin-panel";
    }

    @PatchMapping("/admin-panel/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public String switchUserStatus(@PathVariable UUID userId) {

        userService.switchStatus(userId);

        return "redirect:/users/admin-panel";
    }
}
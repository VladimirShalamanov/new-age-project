package app.web;

import app.security.UserData;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.UserUtils;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/userProfile")
    public ModelAndView getUserProfilePage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());

        ModelAndView model = new ModelAndView("user-profile");
        model.addObject("user", user);

        return model;
    }

//    @GetMapping("/{id}/profile")
//    public ModelAndView getUserProfilePage(@PathVariable UUID id) {
//
//        User user = userService.getById(id);
//        EditUserProfileRequest editUserProfileRequest = DtoMapper.fromUser(user);
//
//        ModelAndView model = new ModelAndView("edit-profile");
//        model.addObject("editUserProfileRequest", editUserProfileRequest);
//        model.addObject("user", user);
//
//        return model;
//    }


//    @PutMapping("/{id}/profile")
//    public ModelAndView updateProfile(@Valid EditProfileRequest editProfileRequest, BindingResult bindingResult, @PathVariable UUID id) {
//
//        if (bindingResult.hasErrors()) {
//            User user = userService.getById(id);
//            ModelAndView modelAndView = new ModelAndView();
//            modelAndView.setViewName("profile-menu");
//            modelAndView.addObject("user", user);
//        }
//
//        userService.updateProfile(id, editProfileRequest);
//
//        return new ModelAndView("redirect:/home");
//        return new ModelAndView("redirect:/PROFILE");
//    }

    // @PreAuthorize("hasRole('ADMIN')") [search in 'ROLE_'] is when we used in 'UserData' - new SimpleGrantedAuthority("ROLE_" + role.name())
    // @PreAuthorize("hasAuthority('ADMIN')") when we use only ONE word - ex. new SimpleGrantedAuthority(role.name())
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    public ModelAndView getUsers() {
//
//        List<User> users = userService.getAll();
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("users");
//        modelAndView.addObject("users", users);
//
//        return modelAndView;
//    }
//
//    @PatchMapping("/{userId}/role")
//    public String switchUserRole(@PathVariable UUID userId) {
//
//        userService.switchRole(userId);
//
//        return  "redirect:/users";
//    }
//
//    @PatchMapping("/{userId}/status")
//    public  String switchUserStatus(@PathVariable UUID userId){
//
//        userService.switchStatus(userId);
//
//        return  "redirect:/users";
//    }
}

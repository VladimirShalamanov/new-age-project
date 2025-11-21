package app.utils;

import app.security.UserData;
import app.user.model.User;
import app.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.experimental.UtilityClass;
import org.springframework.web.servlet.ModelAndView;

@UtilityClass
public class UserUtils {

    public static void handleLoginErrors(ModelAndView model, HttpSession session, String errorMessageInvalidInput) {

        String inactiveUserMessage = (String) session.getAttribute("inactiveUserMessage");

        if (inactiveUserMessage != null) {
            model.addObject("inactiveAccountMessage", inactiveUserMessage);
            session.removeAttribute("inactiveUserMessage");
        } else if (errorMessageInvalidInput != null) {
            model.addObject("errorMessageInvalidInput", "Invalid username or password");
        }
    }

    public static void setGuestOrUser(ModelAndView model, UserData userData, UserService userService) {

        if (userData != null) {
            User user = userService.getById(userData.getUserId());
            model.addObject("user", user);
        }
    }
}

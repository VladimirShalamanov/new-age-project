package app.web;

import app.security.UserData;
import app.user.model.User;
import app.user.service.UserService;
import app.utils.UserUtils;
import app.web.dto.LoginRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class IndexController {

    private final UserService userService;

    @Autowired
    public IndexController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String getIndexPage() {
        return "index";
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage(@RequestParam(name = "loginAttemptMessage", required = false) String message,
                                     @RequestParam(name = "error", required = false) String errorMessageInvalidInput,
                                     HttpSession session) {

        ModelAndView model = new ModelAndView("login");
        model.addObject("loginRequest", new LoginRequest());
        model.addObject("loginAttemptMessage", message);

        UserUtils.handleLoginErrors(model, session, errorMessageInvalidInput);

        return model;
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {

        ModelAndView model = new ModelAndView("register");
        model.addObject("registerRequest", new RegisterRequest());

        return model;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid RegisterRequest registerRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("register");
        }

        userService.register(registerRequest);
        redirectAttributes.addFlashAttribute("successfulRegistration", "You have registered successfully");

        return new ModelAndView("redirect:/login");
    }

    @GetMapping("/home")
    public ModelAndView getHomePage(@AuthenticationPrincipal UserData userData) {

        User user = userService.getById(userData.getUserId());

        ModelAndView model = new ModelAndView("home");

        model.addObject("user", user);
//        modelAndView.addObject("primaryWallet", user.getWallets().stream().filter(Wallet::isMain).findFirst().get());

        return model;
    }
}

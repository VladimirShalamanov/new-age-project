package app.web;

import app.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleException(UserNotFoundException e) {

        return new ModelAndView("not-found");
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView handleException(ProductNotFoundException e) {

        return new ModelAndView("not-found");
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public String handleUsernameAlreadyExistException(UsernameAlreadyExistException e,
                                                      RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessageUsernameAlreadyExist", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public String handleUsernameAlreadyExistException(EmailAlreadyExistException e,
                                                      RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessageEmailAlreadyExist", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(PasswordMatchesException.class)
    public String handlePasswordMatchesException(PasswordMatchesException e,
                                                 RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessagePasswordMatches", e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(ProductAlreadyExistException.class)
    public String handleProductAlreadyExistException(ProductAlreadyExistException e,
                                                 RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessageProductAlreadyExist", e.getMessage());
        return "redirect:/";
    }

    @ExceptionHandler(NotificationRetryFailedException.class)
    public String handleNotificationRetryFailedException(NotificationRetryFailedException e,
                                                         RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessageNotificationRetry", e.getMessage());
        return "redirect:/";
    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler({NoResourceFoundException.class, AccessDeniedException.class})
//    public ModelAndView handleSpringException() {
//
//        return new ModelAndView("not-found");
//    }
//
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public ModelAndView handleLeftoverExceptions(Exception e) {
//
//        return new ModelAndView("internal-server-error");
//    }
}
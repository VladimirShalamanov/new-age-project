package app.web;

import app.notification.client.dto.Email;
import app.notification.client.dto.PreferenceResponse;
import app.notification.service.NotificationService;
import app.security.UserData;
import app.utils.EmailUtils;
import app.web.dto.NotificationPreferenceState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ModelAndView getNotificationPage(@AuthenticationPrincipal UserData userData) {

        ModelAndView model = new ModelAndView("notifications");
        List<Email> emails = notificationService.get10EmailsByUserId(userData.getUserId());
        PreferenceResponse preference = notificationService.getPreferenceByUserId(userData.getUserId());
        long failedEmailsCount = EmailUtils.getFailedEmailsCount(emails);

        model.addObject("emails", emails);
        model.addObject("preference", preference);
        model.addObject("failedEmailsCount", failedEmailsCount);

        return model;
    }

    @PutMapping("/preference")
    public String changeNotificationPreferenceState(@RequestParam("state") NotificationPreferenceState state,
                                                    @AuthenticationPrincipal UserData user) {

        notificationService.updatePreferenceState(state, user.getUserId(), user.getEmail());

        return "redirect:/notifications";
    }

    @DeleteMapping
    public String deleteAllEmails(@AuthenticationPrincipal UserData user) {

        notificationService.deleteAllEmails(user.getUserId());

        return "redirect:/notifications";
    }

    @PutMapping
    public String retryFailedEmails(@AuthenticationPrincipal UserData user) {

        notificationService.retryFailedEmails(user.getUserId());

        return "redirect:/notifications";
    }
}

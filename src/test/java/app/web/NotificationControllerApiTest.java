package app.web;

import app.notification.client.dto.Email;
import app.notification.client.dto.PreferenceResponse;
import app.notification.service.NotificationService;
import app.security.UserData;
import app.user.model.UserRole;
import app.utils.EmailUtils;
import app.web.dto.NotificationPreferenceState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTest {

    @MockitoBean
    NotificationService notificationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getNotificationPage_shouldReturnModelAndViewWithData() throws Exception {

        UserData user = userAuthentication();
        List<Email> emails = List.of(aRandomEmail(user), aRandomEmail(user));
        PreferenceResponse preference = aRandomPreference();

        when(notificationService.get10EmailsByUserId(user.getUserId())).thenReturn(emails);
        when(notificationService.getPreferenceByUserId(user.getUserId())).thenReturn(preference);

        long failedEmailsCount = EmailUtils.getFailedEmailsCount(emails);

        MockHttpServletRequestBuilder request = get("/notifications")
                .with(user(user))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(view().name("notifications"))
                .andExpect(model().attribute("emails", emails))
                .andExpect(model().attribute("preference", preference))
                .andExpect(model().attribute("failedEmailsCount", failedEmailsCount));

        verify(notificationService).get10EmailsByUserId(user.getUserId());
        verify(notificationService).getPreferenceByUserId(user.getUserId());
    }

    @Test
    void changeNotificationPreferenceState_shouldRedirectAndInvokeService() throws Exception {

        UserData user = userAuthentication();

        MockHttpServletRequestBuilder request = put("/notifications/preference")
                .param("state", "ON")
                .with(user(user))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).updatePreferenceState(NotificationPreferenceState.ON, user.getUserId(), user.getEmail());
    }

    @Test
    void deleteAllEmails_shouldRedirectAndInvokeService() throws Exception {

        UserData user = userAuthentication();

        MockHttpServletRequestBuilder request = delete("/notifications")
                .with(user(user))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).deleteAllEmails(user.getUserId());
    }

    @Test
    void retryFailedEmails_shouldRedirectAndInvokeService() throws Exception {

        UserData user = userAuthentication();

        MockHttpServletRequestBuilder request = put("/notifications")
                .with(user(user))
                .with(csrf());

        mockMvc.perform(request)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/notifications"));

        verify(notificationService).retryFailedEmails(user.getUserId());
    }

    public static UserData userAuthentication() {

        return new UserData(UUID.randomUUID(), "11vlad", "1212", "test@abv.bg",
                UserRole.USER, Set.of(), true);
    }

    private static Email aRandomEmail(UserData user) {

        return Email.builder()
                .subject("Test Email")
                .createdOn(LocalDateTime.now())
                .status("SUCCESS")
                .build();
    }

    private static PreferenceResponse aRandomPreference() {

        return PreferenceResponse.builder()
                .notificationEnabled(true)
                .build();
    }
}
package app.utility;

import app.user.model.User;
import app.utils.UserUtils;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class UserUtilsUTest {

    @Mock
    private HttpSession session;

    @Test
    void handleLoginErrors_shouldAddInactiveAccountMessageAndRemoveFromSession() {

        ModelAndView model = new ModelAndView("index");
        String inactiveMessage = "Your account is inactive";

        when(session.getAttribute("inactiveUserMessage")).thenReturn(inactiveMessage);

        UserUtils.handleLoginErrors(model, session, null);

        assertEquals(inactiveMessage, model.getModel().get("inactiveAccountMessage"));
        verify(session).removeAttribute("inactiveUserMessage");
    }

    @Test
    void handleLoginErrors_shouldAddErrorMessageInvalidInput_whenErrorProvidedAndNoInactiveMessage() {

        ModelAndView model = new ModelAndView("index");
        String errorMessage = "some error";

        when(session.getAttribute("inactiveUserMessage")).thenReturn(null);

        UserUtils.handleLoginErrors(model, session, errorMessage);

        assertEquals("Invalid username or password", model.getModel().get("errorMessageInvalidInput"));
        verify(session, never()).removeAttribute(any());
    }

    @Test
    void handleLoginErrors_shouldDoNothing_whenNoErrorAndNoInactiveMessage() {

        ModelAndView model = new ModelAndView("index");

        when(session.getAttribute("inactiveUserMessage")).thenReturn(null);

        UserUtils.handleLoginErrors(model, session, null);

        assertFalse(model.getModel().containsKey("inactiveAccountMessage"));
        assertFalse(model.getModel().containsKey("errorMessageInvalidInput"));
        verify(session, never()).removeAttribute(any());
    }

    @Test
    void hasFullName_returnsTrue_whenFirstAndLastNameArePresent() {

        User user = User.builder()
                .firstName("Vlad")
                .lastName("Vlad")
                .build();

        assertTrue(UserUtils.hasFullName(user));
    }

    @Test
    void hasFullName_returnsFalse_whenFirstNameMissing() {

        User user = User.builder()
                .firstName(null)
                .lastName("Vlad")
                .build();

        assertFalse(UserUtils.hasFullName(user));
    }

    @Test
    void hasFullName_returnsFalse_whenLastNameMissing() {

        User user = User.builder()
                .firstName("Vlad")
                .lastName(null)
                .build();

        assertFalse(UserUtils.hasFullName(user));
    }

    @Test
    void hasFullName_returnsFalse_whenFirstOrLastNameBlank() {

        User user = User.builder()
                .firstName(" ")
                .lastName("Vlad")
                .build();

        assertFalse(UserUtils.hasFullName(user));

        user = User.builder()
                .firstName("Vlad")
                .lastName("   ")
                .build();

        assertFalse(UserUtils.hasFullName(user));
    }

    @Test
    void hasFullAddress_returnsTrue_whenCityAndAddressArePresent() {

        User user = User.builder()
                .city("Berlin")
                .address("vladimir ul 12")
                .build();

        assertTrue(UserUtils.hasFullAddress(user));
    }

    @Test
    void hasFullAddress_returnsFalse_whenCityMissing() {

        User user = User.builder()
                .city(null)
                .address("vladimir ul 12")
                .build();

        assertFalse(UserUtils.hasFullAddress(user));
    }

    @Test
    void hasFullAddress_returnsFalse_whenAddressMissing() {

        User user = User.builder()
                .city("Berlin")
                .address(null)
                .build();

        assertFalse(UserUtils.hasFullAddress(user));
    }

    @Test
    void hasFullAddress_returnsFalse_whenCityOrAddressBlank() {

        User user = User.builder()
                .city(" ")
                .address("vladimir ul 12")
                .build();

        assertFalse(UserUtils.hasFullAddress(user));

        user = User.builder()
                .city("Berlin")
                .address("   ")
                .build();

        assertFalse(UserUtils.hasFullAddress(user));
    }
}
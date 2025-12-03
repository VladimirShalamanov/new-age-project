package app.user;

import app.exception.UserNotFoundException;
import app.notification.service.NotificationService;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.property.UserProperties;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.EditUserProfileRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    //    @Mock
//    private WalletService walletService;
//    @Mock
//    private SubscriptionService subscriptionService;
    @Mock
    private UserProperties userProperties;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private UserService userService;

    @Test
    void whenEditUserDetails_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        EditUserProfileRequest dto = null;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUserProfile(userId, dto));
    }

    @Test
    void whenEditUserDetails_andRepositoryReturnsUserFromTheDatabase_thenUpdateTheUserDetailsAndSaveItToTheDatabase() {

        UUID userId = UUID.randomUUID();
        EditUserProfileRequest dto = EditUserProfileRequest.builder()
                .firstName("Vladimir")
                .lastName("Shalamanov")
                .city("Sofia")
                .address("ul. Vladimir Shalamanov 38")
                .build();
        User userRetrievedFromDatabase = User.builder()
                .id(userId)
                .firstName("Vlad")
                .lastName("Shala")
                .city("SofiaZ")
                .address("ul. Vladimir Shalamanov 22")
                .build();
        when(userRepository.findById(any())).thenReturn(Optional.of(userRetrievedFromDatabase));

        userService.updateUserProfile(userId, dto);

        assertEquals("Vladimir", userRetrievedFromDatabase.getFirstName());
        assertEquals("Shalamanov", userRetrievedFromDatabase.getLastName());
        assertEquals("Sofia", userRetrievedFromDatabase.getCity());
        assertEquals("ul. Vladimir Shalamanov 38", userRetrievedFromDatabase.getAddress());
        verify(userRepository).save(userRetrievedFromDatabase);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsAdmin_thenUserIsUpdatedWithRoleUserAndUpdatedOnNow_andPersistedInTheDatabase() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.USER, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsUser_thenUserIsUpdatedWithRoleAdminAndUpdatedOnNow_andPersistedInTheDatabase() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.USER)
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchRole(userId);

        assertEquals(UserRole.ADMIN, user.getRole());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchRole_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.switchRole(userId));
    }
}
package app.user;

import app.exception.EmailAlreadyExistException;
import app.exception.PasswordMatchesException;
import app.exception.UserNotFoundException;
import app.exception.UsernameAlreadyExistException;
import app.user.model.User;
import app.user.model.UserPermissions;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.user.service.UserService;
import app.web.dto.EditUserProfileRequest;
import app.web.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

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

    @Test
    void whenSwitchPermission_andRepositoryReturnsUserPermissionsACCOUNTANT_thenUserIsUpdatedAndUpdatedOnNow_andPersistedInTheDatabase() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.ADMIN)
                .permissions(new HashSet<>())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchPermission(userId);

        assertTrue(user.getPermissions().contains(UserPermissions.ACCOUNTANT));
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchPermission_andUserHasAccountantPermission_thenPermissionIsRemovedAndUserIsUpdated() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .role(UserRole.ADMIN)
                .permissions(new HashSet<>(Set.of(UserPermissions.ACCOUNTANT)))
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchPermission(userId);

        assertTrue(user.getPermissions().isEmpty());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchPermission_andRepositoryReturnsOptionalEmpty_thenThrowsException() {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.switchPermission(userId));
    }

    @Test
    void whenSwitchStatus_andUserIsActive_thenUserBecomesInactive_andUpdatedOnNow_andPersisted() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .active(true)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchStatus(userId);

        assertFalse(user.isActive());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        verify(userRepository).save(user);
    }

    @Test
    void whenSwitchStatus_andUserIsInactive_thenUserBecomesActive_andUpdatedOnNow_andPersisted() {

        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .active(false)
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.switchStatus(userId);

        assertTrue(user.isActive());
        assertThat(user.getUpdatedOn()).isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));

        verify(userRepository).save(user);
    }

    @Test
    void register_shouldThrowUsernameAlreadyExistException_whenUsernameExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setPassword("pass1234");
        request.setRepeatPassword("pass1234");
        request.setEmail("newemail@test.com");

        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(new User()));

        UsernameAlreadyExistException exception =
                assertThrows(UsernameAlreadyExistException.class, () -> userService.register(request));

        assertEquals("User with [existingUser] username already exist.", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowEmailAlreadyExistException_whenEmailExists() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("pass1234");
        request.setRepeatPassword("pass1234");
        request.setEmail("existing@test.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@test.com")).thenReturn(Optional.of(new User()));

        EmailAlreadyExistException exception =
                assertThrows(EmailAlreadyExistException.class, () -> userService.register(request));

        assertEquals("User with [existing@test.com] email already exist.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldThrowPasswordMatchesException_whenPasswordsDoNotMatch() {

        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setPassword("pass1234");
        request.setRepeatPassword("wrongpass");
        request.setEmail("newemail@test.com");

        when(userRepository.findByUsername("newUser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("newemail@test.com")).thenReturn(Optional.empty());

        PasswordMatchesException exception =
                assertThrows(PasswordMatchesException.class, () -> userService.register(request));

        assertEquals("Invalid password matches.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }
}
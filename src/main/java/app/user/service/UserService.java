package app.user.service;

import app.exception.EmailAlreadyExistException;
import app.exception.PasswordMatchesException;
import app.exception.UserNotFoundException;
import app.exception.UsernameAlreadyExistException;
//import app.notification.service.NotificationService;
import app.security.UserData;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import app.web.dto.EditUserProfileRequest;
import app.web.dto.RegisterRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
//    private final NotificationService notificationService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder
//                       NotificationService notificationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
//        this.notificationService = notificationService;
    }

    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionalUsername = userRepository.findByUsername(registerRequest.getUsername());
        if (optionalUsername.isPresent()) {
            throw new UsernameAlreadyExistException("User with [%s] username already exist.".formatted(registerRequest.getUsername()));
        }

        Optional<User> optionalUserEmail = userRepository.findByEmail(registerRequest.getEmail());
        if (optionalUserEmail.isPresent()) {
            throw new EmailAlreadyExistException("User with [%s] email already exist.".formatted(registerRequest.getEmail()));
        }

        if (!registerRequest.getPassword().equals(registerRequest.getRepeatPassword())) {
            throw new PasswordMatchesException("Invalid password matches.");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(UserRole.USER)
                .active(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

        user = userRepository.save(user);
//        Wallet defaultWallet = walletService.createDefaultWallet(user);
//        Subscription defaultSubscription = subscriptionService.createDefaultSubscription(user);

//        user.setWallets(List.of(defaultWallet));
//        user.setSubscriptions(List.of(defaultSubscription));

        log.info("New user profile was registered in the system for user [%s].".formatted(registerRequest.getUsername()));

        // false (notiEnabled:) - because in "smart-wallet" the user have username at register, not email
//        notificationService.upsertPreference(user.getId(), true, registerRequest.getEmail());

        return user;
    }

    @Cacheable("users")
    public List<User> getAll() {
        return userRepository.findAll();
    }

    public User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with [%s] id not found.".formatted(id)));
    }

    @CacheEvict(value = "users", allEntries = true)
    public void updateUserProfile(UUID id, EditUserProfileRequest editUserProfileRequest) {

        User user = getById(id);

        //            for edit Email
        //            if (editProfileRequest.getEmail() != null && !editProfileRequest.getEmail().isBlank()) {
        //                notificationService.upsertPreference(user.getId(), true, editProfileRequest.getEmail());
        //            } else {
        //                notificationService.upsertPreference(user.getId(), false, null);
        //            }

        user.setFirstName(editUserProfileRequest.getFirstName());
        user.setLastName(editUserProfileRequest.getLastName());
        user.setCity(editUserProfileRequest.getCity());
        user.setAddress(editUserProfileRequest.getAddress());

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchRole(UUID userId) {

        User user = getById(userId);

        if (user.getRole() == UserRole.USER) {
            user.setRole(UserRole.ADMIN);
        } else {
            user.setRole(UserRole.USER);
        }

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    public void switchStatus(UUID userId) {

        User user = getById(userId);
        user.setActive(!user.isActive());

        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);
    }

    // At Login operation, Spring Security will call this method for showing me that someone try to log in.
    // UserDetails - object that store data of Authentication User
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession currentSession = servletRequestAttributes.getRequest().getSession(true);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User with [%s] username not found.".formatted(username)));

        if (!user.isActive()) {
            currentSession.setAttribute("inactiveUserMessage", "This account is blocked!");
        }

        return new UserData(user.getId(), username, user.getPassword(), user.getEmail(), user.getRole(), user.isActive());
    }
}
package com.craft.userAuth.service.impl;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.RegisterRequest;
import com.craft.userAuth.dto.UserDto;
import com.craft.userAuth.dto.UserResponse;
import com.craft.userAuth.model.NotificationEmail;
import com.craft.userAuth.model.entity.User;
import com.craft.userAuth.repository.UserRepository;
import com.craft.userAuth.service.IAuthService;
import com.craft.userAuth.service.IMailService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private IAuthService authService;

    @InjectMocks
    private IMailService mailService;
    @InjectMocks
    private PasswordEncoder passwordEncoder;

    @Test
    void register() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setPassword("Password123");

        // Mocking userRepository.existsByEmail() to return false
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Mocking authService.generateEmailVerificationToken() to return a token
        when(authService.generateEmailVerificationToken(any(User.class))).thenReturn("token");

        // Mocking mailService.sendMail() to do nothing
        doNothing().when(mailService).sendMail(any(NotificationEmail.class));

        // Calling the register method
        userService.register(registerRequest);

        // Verifying that userRepository.save() is called with the correct user object
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals(registerRequest.getEmail()) &&
                        user.getFirstName().equals(registerRequest.getFirstName()) &&
                        user.getLastName().equals(registerRequest.getLastName()) &&
                        passwordEncoder.matches(registerRequest.getPassword(), user.getPassword()) &&
                        !user.isActive()
        ));

        // Verifying that authService.generateEmailVerificationToken() is called with the correct user object
        verify(authService).generateEmailVerificationToken(argThat(user ->
                user.getEmail().equals(registerRequest.getEmail()) &&
                        user.getFirstName().equals(registerRequest.getFirstName()) &&
                        user.getLastName().equals(registerRequest.getLastName()) &&
                        passwordEncoder.matches(registerRequest.getPassword(), user.getPassword()) &&
                        !user.isActive()
        ));

        // Verifying that mailService.sendMail() is called with the correct notification email object
        verify(mailService).sendMail(argThat(notificationEmail ->
                notificationEmail.getSubject().equals(Constants.Email.ACTIVATE_ACCOUNT_SUBJECT) &&
                        notificationEmail.getRecipient().equals(registerRequest.getEmail()) &&
                        notificationEmail.getBody().equals(MessageFormat.format(Constants.Email.ACTIVATE_ACCOUNT_BODY, "token"))
        ));
    }

    @Test
    void getRecoveryCode() throws Exception {
        User user = new User();
        user.setEmail("test@example.com");
        Mockito.when(userRepository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(user));


        Mockito.when(authService.retrievePasswordRecoveryCode(Mockito.any(User.class))).thenReturn("123456");





        // Call the method under test
        try {
            userService.getRecoveryCode("test@example.com");
        } catch (Exception e) {
            fail("Exception should not be thrown");
        }

        // Verify that UserRepository.findByEmail was called with the correct email
        Mockito.verify(userRepository).findByEmail("test@example.com");

        // Verify that AuthService.retrievePasswordRecoveryCode was called with the correct user
        Mockito.verify(authService).retrievePasswordRecoveryCode(user);

        // Verify that MailService.sendMail was called with the correct parameters
        Mockito.verify(mailService).sendMail(
                new NotificationEmail(
                        Constants.Email.PASSWORD_RECOVERY_SUBJECT,
                        "test@example.com",
                        "Password reset recovery code, please use below recovery code for password reset :  123456"
                )
        );
    }

    @Test
    void getUserDetails() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setFirstName("John");
        user.setLastName("Doe");

        UserService userService = new UserService();
        UserDto userDetails = userService.getUserDetails(user);

        assertEquals(user.getEmail(), userDetails.getEmail());
        assertEquals(user.getFirstName(), userDetails.getFirstName());
        assertEquals(user.getLastName(), userDetails.getLastName());

    }

    @Test
    void getUserByEmail() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        // Mock the userRepository.findByEmail method to return the user object
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getUserByEmail(email);

        // Assert
        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void getAllUsers() {
        UserService userService = new UserService();
        List<User> users = new ArrayList<>();
        User user1 = new User("1", "John", "Doe", "password1", "john@example.com", Instant.now(), true);
        User user2 = new User("2", "Jane", "Smith", "password2", "jane@example.com", Instant.now(), true);
        users.add(user1);
        users.add(user2);
        Page<User> page = new PageImpl<>(users);
        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);


        // Act
        UserResponse result = userService.getAllUsers(0, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getUsers().size());
        assertEquals("john@example.com", result.getUsers().get(0).getEmail());
        assertEquals("John", result.getUsers().get(0).getFirstName());
        assertEquals("Doe", result.getUsers().get(0).getLastName());
        assertEquals("jane@example.com", result.getUsers().get(1).getEmail());
        assertEquals("Jane", result.getUsers().get(1).getFirstName());
        assertEquals("Smith", result.getUsers().get(1).getLastName());

    }
}
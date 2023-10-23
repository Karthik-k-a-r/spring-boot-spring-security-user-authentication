package com.craft.authentication.service.implementation;

import com.craft.authentication.dto.request.RegisterRequest;
import com.craft.authentication.dto.response.UserDto;
import com.craft.authentication.dto.response.UserResponse;
import com.craft.authentication.enums.Roles;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.NotificationEmail;
import com.craft.authentication.model.entity.Role;
import com.craft.authentication.model.entity.User;
import com.craft.authentication.repository.RoleRepository;
import com.craft.authentication.repository.UserRepository;
import com.craft.authentication.service.IAuthService;
import com.craft.authentication.service.IMailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {UserService.class})
@ExtendWith(SpringExtension.class)
class UserServiceTest {
    @MockBean
    private IAuthService iAuthService;

    @MockBean
    private IMailService iMailService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Test
    void testRegister() throws Exception {
        Role role = Role.builder()
                .id(1L)
                .name(Roles.USER)
                .users(new ArrayList<>())
                .build();

        User user = User.builder()
                .active(true)
                .created(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
                .email("test@example.com")
                .firstName("test")
                .lastName("demo")
                .password("Test@123")
                .role(role)
                .userId("10")
                .build();

        when(userRepository.existsByEmail(Mockito.any())).thenReturn(false);
        when(userRepository.save(Mockito.any())).thenReturn(user);
        when(iAuthService.generateEmailVerificationToken(Mockito.any())).thenReturn("1234");
        doNothing().when(iMailService).sendMail(Mockito.any());
        when(passwordEncoder.encode(Mockito.any())).thenReturn("secret");
        assertEquals(
                "Your account has been successfully created.Please verify Email by the link sent on your Email" + " address.",
                userService.register(new RegisterRequest("test@example.com", "test", "demo", "Test@123")).getSuccess());
        verify(userRepository).existsByEmail(Mockito.any());
        verify(userRepository).save(Mockito.any());
        verify(iAuthService).generateEmailVerificationToken(Mockito.any());
        verify(iMailService).sendMail(Mockito.any());
        verify(roleRepository).findByName(Mockito.any());
        verify(passwordEncoder).encode(Mockito.any());
    }

    @Test
    void testRegisterThrowsException() throws Exception {
        when(userRepository.existsByEmail(Mockito.any())).thenReturn(true);
        assertThrows(CustomException.class,
                () -> userService.register(new RegisterRequest("test@example.com", "test", "demo", "Test@123")));
        verify(userRepository).existsByEmail(Mockito.any());
    }

    @Test
    void testGetRecoveryCode() throws Exception {
        Role role = Role.builder()
                .id(1L)
                .name(Roles.USER)
                .users(new ArrayList<>())
                .build();

        User user = User.builder()
                .active(true)
                .created(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
                .email("test@example.com")
                .firstName("test")
                .lastName("demo")
                .password("Test@123")
                .role(role)
                .userId("10")
                .build();
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(Mockito.<String>any())).thenReturn(optionalUser);
        when(iAuthService.retrievePasswordRecoveryCode(Mockito.any()))
                .thenReturn("Retrieve Password Recovery Code");
        doNothing().when(iMailService).sendMail(Mockito.any());
        assertEquals("Password reset recovery code is successfully sent to your registered email. Kindly check.",
                userService.getRecoveryCode("test@example.com").getSuccess());
        verify(userRepository).findByEmail(Mockito.any());
        verify(iAuthService).retrievePasswordRecoveryCode(Mockito.any());
        verify(iMailService).sendMail(Mockito.any());
    }

    @Test
    void testGetRecoveryCodeThrowsException() throws Exception {
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByEmail(Mockito.any())).thenReturn(emptyResult);
        assertThrows(CustomException.class, () -> userService.getRecoveryCode("test@example.com"));
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testGetUserByEmail() {
        Role role = Role.builder()
                .id(1L)
                .name(Roles.USER)
                .users(new ArrayList<>())
                .build();

        User user = User.builder()
                .active(true)
                .created(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant())
                .email("test@example.com")
                .firstName("test")
                .lastName("demo")
                .password("Test@123")
                .role(role)
                .userId("10")
                .build();

        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        UserDto actualUserByEmail = userService.getUserByEmail("test@example.com");
        assertEquals("test@example.com", actualUserByEmail.getEmail());
        assertEquals("test", actualUserByEmail.getFirstName());
        assertEquals("demo", actualUserByEmail.getLastName());
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testGetUserByEmailThrowsException() {
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByEmail(Mockito.any())).thenReturn(emptyResult);
        assertThrows(CustomException.class, () -> userService.getUserByEmail("test@example.com"));
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testGetAllUsers() {
        List<User> content = new ArrayList<>();
        List<UserDto> users = new ArrayList<>();
        when(userRepository.findAll(Mockito.<Pageable>any())).thenReturn(new PageImpl<>(content));
        UserResponse actualAllUsers = userService.getAllUsers(1, 3);
        assertEquals(0, actualAllUsers.getPageNo());
        assertTrue(actualAllUsers.isLast());
        assertEquals(users, actualAllUsers.getUsers());
        assertEquals(1, actualAllUsers.getTotalPages());
        assertEquals(0L, actualAllUsers.getTotalElements());
        assertEquals(0, actualAllUsers.getPageSize());
        verify(userRepository).findAll(Mockito.<Pageable>any());
    }

    @Test
    void testGetAllUsersThrowsException() {
        when(userRepository.findAll(Mockito.<Pageable>any()))
                .thenThrow(new CustomException("An error occurred", "An error occurred"));
        assertThrows(CustomException.class, () -> userService.getAllUsers(1, 3));
        verify(userRepository).findAll(Mockito.<Pageable>any());
    }

}


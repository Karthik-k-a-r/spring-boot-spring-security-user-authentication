package com.craft.authentication.service.implementation;

import com.craft.authentication.dto.request.LoginRequest;
import com.craft.authentication.dto.request.PasswordResetRequest;
import com.craft.authentication.enums.Roles;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.entity.Role;
import com.craft.authentication.model.entity.User;
import com.craft.authentication.model.entity.VerificationToken;
import com.craft.authentication.repository.UserRepository;
import com.craft.authentication.repository.VerificationTokenRepository;
import com.craft.authentication.service.IJwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {AuthService.class})
@ExtendWith(SpringExtension.class)
class AuthServiceTest {
    @Autowired
    private AuthService authService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private IJwtService iJwtService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private VerificationTokenRepository verificationTokenRepository;

    @Test
    void testGenerateEmailVerificationToken() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();

        when(verificationTokenRepository.save(Mockito.any())).thenReturn(verificationToken);
        authService.generateEmailVerificationToken(user);
    }

    @Test
    void testGenerateEmailVerificationTokenThrowsException() {
        when(verificationTokenRepository.save(Mockito.any()))
                .thenThrow(new CustomException("An error occurred", "An error occurred"));
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

        assertThrows(CustomException.class, () -> authService.generateEmailVerificationToken(user));
        verify(verificationTokenRepository).save(Mockito.any());
    }

    @Test
    void testVerifyAccount() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();

        Optional<VerificationToken> optionalVerificationToken = Optional.of(verificationToken);
        when(verificationTokenRepository.findByToken(Mockito.any())).thenReturn(optionalVerificationToken);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.save(Mockito.any())).thenReturn(user);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        assertEquals(" Your account has been successfully verified and activated.",
                authService.verifyAccount("1234").getSuccess());
        verify(verificationTokenRepository).findByToken(Mockito.any());
        verify(userRepository).save(Mockito.any());
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testVerifyAccountThrowsException() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();
        Optional<VerificationToken> optionalVerificationToken = Optional.of(verificationToken);
        when(verificationTokenRepository.findByToken(Mockito.any())).thenReturn(optionalVerificationToken);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.save(Mockito.any()))
                .thenThrow(new CustomException("An error occurred", "An error occurred"));
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        assertThrows(CustomException.class, () -> authService.verifyAccount("1234"));
        verify(verificationTokenRepository).findByToken(Mockito.any());
        verify(userRepository).save(Mockito.any());
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testRetrievePasswordRecoveryCode() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();

        Optional<VerificationToken> optionalVerificationToken = Optional.of(verificationToken);
        when(verificationTokenRepository.findByUserUserId(Mockito.any())).thenReturn(optionalVerificationToken);
        assertEquals("1234", authService.retrievePasswordRecoveryCode(user));
        verify(verificationTokenRepository).findByUserUserId(Mockito.any());
    }

    @Test
    void testResetPassword() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();
        Optional<VerificationToken> optionalVerificationToken = Optional.of(verificationToken);
        when(verificationTokenRepository.findByToken(Mockito.any())).thenReturn(optionalVerificationToken);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        when(passwordEncoder.encode(Mockito.<CharSequence>any())).thenReturn("secret");
        assertEquals("Your password has been changed successfully",
                authService.resetPassword(new PasswordResetRequest("Recovery Code", "1234")).getSuccess());
        verify(verificationTokenRepository).findByToken(Mockito.any());
        verify(userRepository).save(Mockito.any());
        verify(userRepository).findByEmail(Mockito.any());
        verify(passwordEncoder).encode(Mockito.any());
    }
    @Test
    void testResetPasswordThrowsException() {
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

        VerificationToken verificationToken = VerificationToken.builder()
                .id(1L)
                .token("1234")
                .user(user)
                .build();
        Optional<VerificationToken> optionalVerificationToken = Optional.of(verificationToken);
        when(verificationTokenRepository.findByToken(Mockito.any())).thenReturn(optionalVerificationToken);
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        when(passwordEncoder.encode(Mockito.any()))
                .thenThrow(new CustomException("An error occurred", "An error occurred"));
        assertThrows(CustomException.class,
                () -> authService.resetPassword(new PasswordResetRequest("Recovery Code", "1234")));
        verify(verificationTokenRepository).findByToken(Mockito.any());
        verify(userRepository).findByEmail(Mockito.any());
        verify(passwordEncoder).encode(Mockito.any());
    }

    @Test
    void testLogin() throws AuthenticationException {
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
        when(iJwtService.generateToken(Mockito.any())).thenReturn("1234");
        when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(new BearerTokenAuthenticationToken("1234"));
        assertEquals("Bearer 1234", authService.login(new LoginRequest("test@example.com", "Test@123")).getToken());
        verify(userRepository).findByEmail(Mockito.any());
        verify(iJwtService).generateToken(Mockito.any());
        verify(authenticationManager).authenticate(Mockito.any());
    }

    @Test
    void testLoginThrowsException() throws AuthenticationException {
        when(authenticationManager.authenticate(Mockito.any()))
                .thenThrow(new CustomException("An error occurred", "An error occurred"));
        assertThrows(CustomException.class,
                () -> authService.login(new LoginRequest("test@example.com", "Test@123")));
        verify(authenticationManager).authenticate(Mockito.any());
    }

    @Test
    void testLoginDemo() throws AuthenticationException {
        Role role = Role.builder()
                .id(1L)
                .name(Roles.USER)
                .users(new ArrayList<>())
                .build();
        User user = mock(User.class);
        when(user.isActive()).thenReturn(false);
        doNothing().when(user).setActive(anyBoolean());
        doNothing().when(user).setCreated(Mockito.any());
        doNothing().when(user).setEmail(Mockito.any());
        doNothing().when(user).setFirstName(Mockito.any());
        doNothing().when(user).setLastName(Mockito.any());
        doNothing().when(user).setPassword(Mockito.any());
        doNothing().when(user).setRole(Mockito.any());
        doNothing().when(user).setUserId(Mockito.any());
        user.setActive(true);
        user.setCreated(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        user.setEmail("test@example.com");
        user.setFirstName("test");
        user.setLastName("demo");
        user.setPassword("Test@123");
        user.setRole(role);
        user.setUserId("10");
        Optional<User> optionalUser = Optional.of(user);
        when(userRepository.findByEmail(Mockito.any())).thenReturn(optionalUser);
        when(authenticationManager.authenticate(Mockito.any()))
                .thenReturn(new BearerTokenAuthenticationToken("1234"));
        assertThrows(CustomException.class,
                () -> authService.login(new LoginRequest("test@example.com", "Test@123")));
    }

}


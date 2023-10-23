package com.craft.authentication.service.implementation;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.craft.authentication.enums.Roles;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.entity.Role;
import com.craft.authentication.model.entity.User;
import com.craft.authentication.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {UserDetailsServiceImplementation.class})
@ExtendWith(SpringExtension.class)
class UserDetailsServiceImplementationTest {
    @Autowired
    private UserDetailsServiceImplementation userDetailsServiceImplementation;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testLoadUserByUsername() {
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
        assertSame(user, userDetailsServiceImplementation.loadUserByUsername("test@example.com"));
        verify(userRepository).findByEmail(Mockito.any());
    }

    @Test
    void testLoadUserByUsernameThrowsException() {
        Optional<User> emptyResult = Optional.empty();
        when(userRepository.findByEmail(Mockito.any())).thenReturn(emptyResult);
        assertThrows(CustomException.class, () -> userDetailsServiceImplementation.loadUserByUsername("test@example.com"));
        verify(userRepository).findByEmail(Mockito.any());
    }

}


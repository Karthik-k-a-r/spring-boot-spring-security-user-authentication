package com.craft.authentication.service.implementation;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImplementation implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {

        return userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,username),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));
    }
}

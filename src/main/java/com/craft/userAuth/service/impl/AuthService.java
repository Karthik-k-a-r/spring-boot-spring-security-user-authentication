package com.craft.userAuth.service.impl;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.AuthenticationResponse;
import com.craft.userAuth.dto.LoginRequest;
import com.craft.userAuth.dto.PasswordResetRequest;
import com.craft.userAuth.exception.CustomException;
import com.craft.userAuth.model.entity.User;
import com.craft.userAuth.model.entity.VerificationToken;
import com.craft.userAuth.repository.UserRepository;
import com.craft.userAuth.repository.VerificationTokenRepository;
import com.craft.userAuth.security.JwtProvider;
import com.craft.userAuth.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
public class AuthService implements IAuthService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String generateEmailVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));

        user.setActive(true);

        userRepository.save(user);
    }

    public void verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(Constants.ErrorMessages.INVALID_TOKEN,
                        Constants.ErrorCodes.INVALID_TOKEN_CODE));

        fetchUserAndEnable(verificationToken);
    }

    @Override
    public String retrievePasswordRecoveryCode(User user) {
        String userId=user.getUserId();
        VerificationToken verificationToken = verificationTokenRepository.findByUserUserId(userId)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,user.getEmail()),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));

        return verificationToken.getToken();
    }

    @Override
    public void resetPassword(PasswordResetRequest passwordResetRequest) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(passwordResetRequest.getRecoveryCode())
                .orElseThrow(() -> new CustomException(Constants.ErrorMessages.INVALID_TOKEN,
                Constants.ErrorCodes.INVALID_TOKEN_CODE));
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                Constants.ErrorCodes.USER_NOT_FOUND_CODE));
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));

        userRepository.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String token = jwtProvider.generateToken(authenticate);
        return AuthenticationResponse.builder()
                .authenticationToken("Bearer " +token)
                .build();
    }

}

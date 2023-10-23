package com.craft.authentication.service.implementation;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.dto.response.AuthenticationResponse;
import com.craft.authentication.dto.request.LoginRequest;
import com.craft.authentication.dto.request.PasswordResetRequest;
import com.craft.authentication.dto.SuccessResponse;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.entity.User;
import com.craft.authentication.model.entity.VerificationToken;
import com.craft.authentication.repository.UserRepository;
import com.craft.authentication.repository.VerificationTokenRepository;
import com.craft.authentication.service.IAuthService;
import com.craft.authentication.service.IJwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IJwtService jwtService;

    @Override
    public String generateEmailVerificationToken(User user) {
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken=VerificationToken.builder()
                .token(token)
                .user(user)
                .build();

        verificationTokenRepository.save(verificationToken);
        return token;
    }

    @Override
    public SuccessResponse verifyAccount(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(Constants.ErrorMessages.INVALID_TOKEN,
                        Constants.ErrorCodes.INVALID_TOKEN_CODE));

        fetchUserAndEnable(verificationToken);

        return SuccessResponse.builder()
                .success(Constants.SuccessMessages.USER_ACCOUNT_VERIFIED)
                .build();
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
    public SuccessResponse resetPassword(PasswordResetRequest passwordResetRequest) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(passwordResetRequest.getRecoveryCode())
                .orElseThrow(() -> new CustomException(Constants.ErrorMessages.INVALID_TOKEN,
                Constants.ErrorCodes.INVALID_TOKEN_CODE));
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                Constants.ErrorCodes.USER_NOT_FOUND_CODE));
        user.setPassword(passwordEncoder.encode(passwordResetRequest.getNewPassword()));

        userRepository.save(user);

        return SuccessResponse.builder()
                .success(Constants.SuccessMessages.PASSWORD_CHANGED)
                .build();
    }
    @Override
    public AuthenticationResponse login(LoginRequest loginRequest) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,loginRequest.getEmail()),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));
        if(!user.isActive()){
            throw new CustomException(Constants.ErrorMessages.ACCOUNT_NOT_VERIFIED,
                    Constants.ErrorCodes.ACCOUNT_NOT_VERIFIED_CODE);
        }
        String jwt = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(Constants.Jwt.PREFIX+jwt)
                .build();
    }

    private void fetchUserAndEnable(VerificationToken verificationToken) {
        String email = verificationToken.getUser().getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));

        user.setActive(true);

        userRepository.save(user);
    }

}

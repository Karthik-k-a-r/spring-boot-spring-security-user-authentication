package com.craft.authentication.service;

import com.craft.authentication.dto.SuccessResponse;
import com.craft.authentication.dto.request.LoginRequest;
import com.craft.authentication.dto.request.PasswordResetRequest;
import com.craft.authentication.dto.response.AuthenticationResponse;
import com.craft.authentication.model.entity.User;

public interface IAuthService {

    String generateEmailVerificationToken(User user);

    SuccessResponse verifyAccount(String token);

    String retrievePasswordRecoveryCode(User user);

    SuccessResponse resetPassword(PasswordResetRequest passwordResetRequest);

    AuthenticationResponse login(LoginRequest loginRequest);
}

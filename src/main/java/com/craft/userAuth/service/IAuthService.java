package com.craft.userAuth.service;

//import com.craft.userAuth.dto.AuthenticationResponse;
import com.craft.userAuth.dto.AuthenticationResponse;
import com.craft.userAuth.dto.LoginRequest;
import com.craft.userAuth.dto.PasswordResetRequest;
import com.craft.userAuth.model.entity.User;
import com.craft.userAuth.model.entity.VerificationToken;

public interface IAuthService {

    String generateEmailVerificationToken(User user);

    void verifyAccount(String token);

    void fetchUserAndEnable(VerificationToken verificationToken);

    String retrievePasswordRecoveryCode(User user);

    void resetPassword(PasswordResetRequest passwordResetRequest);

    AuthenticationResponse login(LoginRequest loginRequest);
}

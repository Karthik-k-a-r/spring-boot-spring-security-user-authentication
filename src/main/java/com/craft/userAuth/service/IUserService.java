package com.craft.userAuth.service;

import com.craft.userAuth.dto.PasswordResetRequest;
import com.craft.userAuth.dto.RegisterRequest;
import com.craft.userAuth.dto.UserResponse;
import com.craft.userAuth.model.User;
import com.craft.userAuth.model.VerificationToken;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Optional;

public interface IUserService {
    ResponseEntity<String> register(RegisterRequest registerRequest) throws Exception;
    void getRecoveryCode(String email) throws Exception;

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

}

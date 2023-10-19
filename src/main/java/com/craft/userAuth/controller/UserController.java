package com.craft.userAuth.controller;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.*;
import com.craft.userAuth.service.IAuthService;
import com.craft.userAuth.service.IUserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;
    @PostMapping("")
    public ResponseEntity<String> register(@RequestBody @Valid RegisterRequest registerRequest) throws Exception {
        userService.register(registerRequest);
        return new ResponseEntity<>(Constants.SuccessMessages.USER_ACCOUNT_CREATED,
                HttpStatus.CREATED);
    }

    @GetMapping("/account/verify/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>(Constants.SuccessMessages.USER_ACCOUNT_VERIFIED,
                OK);
    }

    @GetMapping("/account/recovery/{email}")
    public ResponseEntity<String> getRecoveryCode(@PathVariable String email ) throws Exception {
        userService.getRecoveryCode(email);
        return new ResponseEntity<>(Constants.SuccessMessages.RECOVERY_CODE_SENT,
                OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
        return new ResponseEntity<>(Constants.SuccessMessages.PASSWORD_CHANGED,
                OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest),
                OK);
    }

}

package com.craft.authentication.controller;

import com.craft.authentication.dto.request.LoginRequest;
import com.craft.authentication.dto.request.PasswordResetRequest;
import com.craft.authentication.dto.request.RegisterRequest;
import com.craft.authentication.service.IAuthService;
import com.craft.authentication.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final IAuthService authService;
    @PostMapping
    public ResponseEntity<Object> register(@RequestBody @Valid RegisterRequest registerRequest) throws Exception {
        return new ResponseEntity<>(userService.register(registerRequest),
                HttpStatus.CREATED);
    }

    @GetMapping("/account/verify/{token}")
    public ResponseEntity<Object> verifyAccount(@PathVariable String token) {
        return new ResponseEntity<>(authService.verifyAccount(token),
                OK);
    }

    @GetMapping("/account/recovery/{email}")
    public ResponseEntity<Object> getRecoveryCode(@PathVariable String email ) throws Exception {
        return new ResponseEntity<>(userService.getRecoveryCode(email),
                OK);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Object> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        return new ResponseEntity<>(authService.resetPassword(passwordResetRequest),
                OK);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> authenticate(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.login(loginRequest),
                OK);
    }

}

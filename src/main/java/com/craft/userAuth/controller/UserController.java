package com.craft.userAuth.controller;

import com.craft.userAuth.dto.*;
import com.craft.userAuth.service.IAuthService;
import com.craft.userAuth.service.IUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/v1/user")
@AllArgsConstructor
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IAuthService authService;
    @PostMapping("/")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) throws Exception {
        //        userService.register(registerRequest);
//        return new ResponseEntity<>("User Registration Successful",
//                OK);
        return userService.register(registerRequest);
    }

    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount(@PathVariable String token) {
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully", OK);
    }

    @GetMapping("recovery/{email}")
    public ResponseEntity<String> getRecoveryCode(@PathVariable String email ) throws Exception {
        userService.getRecoveryCode(email);
        return new ResponseEntity<>("Password Recovery Code sent Successful",
                OK);
    }

    @PatchMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequest passwordResetRequest) {
        authService.resetPassword(passwordResetRequest);
        return new ResponseEntity<>("Password Reset Successful",
                OK);
    }

    @PostMapping("/authenticate")
    public AuthenticationResponse authenticate(@RequestBody LoginRequest loginRequest) {
        return authService.login(loginRequest);
    }

    @GetMapping(params = "email")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestParam String email) {
        return status(HttpStatus.OK).body(userService.getUserByEmail(email));
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return status(HttpStatus.OK).body(userService.getAllUsers());
    }

}

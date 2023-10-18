package com.craft.userAuth.service.impl;

import com.craft.userAuth.dto.RegisterRequest;
import com.craft.userAuth.dto.UserResponse;
import com.craft.userAuth.model.NotificationEmail;
import com.craft.userAuth.model.User;
import com.craft.userAuth.repository.UserRepository;
import com.craft.userAuth.service.IAuthService;
import com.craft.userAuth.service.IMailService;
import com.craft.userAuth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;


@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IAuthService authService;

    @Autowired
    private IMailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Override
    public ResponseEntity<String> register(RegisterRequest registerRequest) throws Exception {
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            return new ResponseEntity<>("Error: Email is already taken",
                    BAD_REQUEST);
        }else {
            User user = new User();
            user.setFirstName(registerRequest.getFirstName());
            user.setEmail(registerRequest.getEmail());
            user.setLastName(registerRequest.getLastName());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setCreated(Instant.now());
            user.setActive(false);

            userRepository.save(user);

            String token = authService.generateEmailVerificationToken(user);

            mailService.sendMail(new NotificationEmail("Please Activate your Account",
                    user.getEmail(), "Thank you for signing up, " +
                    "please click on the below url to activate your account : " +
                    "http://localhost:8080/api/v1/user/accountVerification/" + token));
            return new ResponseEntity<>("User Registration Successful",
                  OK);
        }
    }

    @Override
    public void getRecoveryCode(String email) throws Exception {
        User user=userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No user " +
                        "Found with Email : " + email));

        String token = authService.generatePasswordRecoveryCode(user);

        mailService.sendMail(new NotificationEmail("Please Reset your Password",
                email, "Password recovery code, " +
                "please use below recovery code for password reset : " + token));
    }

    public UserResponse getUserResponse(User user){
        UserResponse userResponse=new UserResponse();
        userResponse.setEmail(user.getEmail());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setLastName(user.getLastName());
        return userResponse;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("No user " +
                "Found with Email : " + email));

        return getUserResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users=userRepository.findAll();
        return users.stream().map(this::getUserResponse).collect(Collectors.toList());
    }

}

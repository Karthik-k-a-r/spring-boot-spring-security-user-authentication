package com.craft.userAuth.service.impl;

import com.craft.userAuth.constants.Constants;
import com.craft.userAuth.dto.RegisterRequest;
import com.craft.userAuth.dto.UserDto;
import com.craft.userAuth.dto.UserResponse;
import com.craft.userAuth.enums.Roles;
import com.craft.userAuth.exception.CustomException;
import com.craft.userAuth.model.NotificationEmail;
import com.craft.userAuth.model.entity.Role;
import com.craft.userAuth.model.entity.User;
import com.craft.userAuth.repository.RoleRepository;
import com.craft.userAuth.repository.UserRepository;
import com.craft.userAuth.service.IAuthService;
import com.craft.userAuth.service.IMailService;
import com.craft.userAuth.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;


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
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public void register(RegisterRequest registerRequest) throws Exception {

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(Constants.ErrorMessages.DUPLICATE_EMAIL,
                    Constants.ErrorCodes.DUPLICATE_EMAIL_CODE);
        }

        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setEmail(registerRequest.getEmail());
        user.setLastName(registerRequest.getLastName());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setCreated(Instant.now());
        user.setActive(false);
        Role role=roleRepository.findByName(Roles.USER);
        user.setRole(role);

        userRepository.save(user);

        String token = authService.generateEmailVerificationToken(user);

        mailService.sendMail(
                new NotificationEmail(
                        Constants.Email.ACTIVATE_ACCOUNT_SUBJECT,
                        user.getEmail(),
                        MessageFormat.format(Constants.Email.ACTIVATE_ACCOUNT_BODY, token)
                )
        );
    }

    @Override
    public void getRecoveryCode(String email) throws Exception {
        User user=userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                        Constants.ErrorCodes.USER_NOT_FOUND_CODE));

        String token = authService.retrievePasswordRecoveryCode(user);

        mailService.sendMail(
                new NotificationEmail(
                        Constants.Email.PASSWORD_RECOVERY_SUBJECT,
                        email,
                        MessageFormat.format(Constants.Email.PASSWORD_RECOVERY_BODY,token)
                )
        );
    }

    public UserDto getUserDetails(User user){
        UserDto userDetails=new UserDto();
        userDetails.setEmail(user.getEmail());
        userDetails.setFirstName(user.getFirstName());
        userDetails.setLastName(user.getLastName());
        return userDetails;
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MessageFormat.format(Constants.ErrorMessages.USER_NOT_FOUND,email),
                Constants.ErrorCodes.USER_NOT_FOUND_CODE));

        return getUserDetails(user);
    }

    @Override
    public UserResponse getAllUsers(int pageNo,int pageSize) {
        Pageable pageable= PageRequest.of(pageNo,pageSize);
        Page<User> pages=userRepository.findAll(pageable);
        List<User> listOfUsers=pages.getContent();
        List<UserDto> userDetails=listOfUsers.stream().map(this::getUserDetails).toList();
        return UserResponse.builder()
                .users(userDetails)
                .pageNo(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .last(pages.isLast())
                .build();
    }

}

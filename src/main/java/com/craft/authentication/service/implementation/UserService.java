package com.craft.authentication.service.implementation;

import com.craft.authentication.constants.Constants;
import com.craft.authentication.dto.request.RegisterRequest;
import com.craft.authentication.dto.response.UserDto;
import com.craft.authentication.dto.SuccessResponse;
import com.craft.authentication.dto.response.UserResponse;
import com.craft.authentication.enums.Roles;
import com.craft.authentication.exception.CustomException;
import com.craft.authentication.model.NotificationEmail;
import com.craft.authentication.model.entity.User;
import com.craft.authentication.repository.RoleRepository;
import com.craft.authentication.repository.UserRepository;
import com.craft.authentication.service.IAuthService;
import com.craft.authentication.service.IMailService;
import com.craft.authentication.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final IAuthService authService;
    private final IMailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    @Override
    public SuccessResponse register(RegisterRequest registerRequest) throws Exception {

        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new CustomException(Constants.ErrorMessages.DUPLICATE_EMAIL,
                    Constants.ErrorCodes.DUPLICATE_EMAIL_CODE);
        }
        com.craft.authentication.model.entity.Role role=roleRepository.findByName(Roles.USER);

        User user = User.builder()
                .firstName(registerRequest.getFirstName())
                .email(registerRequest.getEmail())
                .lastName(registerRequest.getLastName())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(role)
                .created(Instant.now())
                .active(false)
                .build();

        userRepository.save(user);

        String token = authService.generateEmailVerificationToken(user);

        mailService.sendMail(
                new NotificationEmail(
                        Constants.Email.VERIFY_ACCOUNT_SUBJECT,
                        user.getEmail(),
                        MessageFormat.format(Constants.Email.VERIFY_ACCOUNT_BODY, token)
                )
        );

        return SuccessResponse.builder()
                .success(Constants.SuccessMessages.USER_ACCOUNT_CREATED)
                .build();
    }

    @Override
    public SuccessResponse getRecoveryCode(String email) throws Exception {
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

        return SuccessResponse.builder()
                .success(Constants.SuccessMessages.RECOVERY_CODE_SENT)
                .build();
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
        List<UserDto> userDetails=listOfUsers.stream().map(this::getUserDetails).collect(Collectors.toList());
        return UserResponse.builder()
                .users(userDetails)
                .pageNo(pages.getNumber())
                .pageSize(pages.getSize())
                .totalElements(pages.getTotalElements())
                .totalPages(pages.getTotalPages())
                .last(pages.isLast())
                .build();
    }

    private UserDto getUserDetails(User user){

        return UserDto.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }

}

package com.craft.userAuth.dto;

import com.craft.userAuth.constants.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @Email
    @NotEmpty(message = Constants.ErrorMessages.EMAIL_REQUIRED)
    private String email;

    @NotEmpty(message = Constants.ErrorMessages.PASSWORD_REQUIRED)
    @Pattern(regexp = Constants.RegExpression.PASSWORD, message = Constants.ErrorMessages.INVALID_PASSWORD)
    private String password;
}

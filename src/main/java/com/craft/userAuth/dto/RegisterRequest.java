package com.craft.userAuth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.craft.userAuth.constants.Constants;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @Email
    @NotEmpty(message = Constants.ErrorMessages.EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = Constants.ErrorMessages.FIRST_NAME_REQUIRED)
    private String firstName;

    private String lastName;

    @NotBlank(message = Constants.ErrorMessages.PASSWORD_REQUIRED)
    @Pattern(regexp = Constants.RegExpression.PASSWORD, message = Constants.ErrorMessages.INVALID_PASSWORD)
    private String password;

}

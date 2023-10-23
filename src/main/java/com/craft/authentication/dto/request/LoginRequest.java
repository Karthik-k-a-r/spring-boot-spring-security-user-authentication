package com.craft.authentication.dto.request;

import com.craft.authentication.constants.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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

package com.craft.userAuth.dto;

import com.craft.userAuth.constants.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordResetRequest {
    @NotEmpty(message = Constants.ErrorMessage.RECOVERY_CODE_REQUIRED)
    private String recoveryCode;

    @NotEmpty(message = Constants.ErrorMessage.PASSWORD_REQUIRED)
    @Pattern(regexp = Constants.RegExpression.PASSWORD, message = Constants.ErrorMessage.INVALID_PASSWORD)
    private String newPassword;
}

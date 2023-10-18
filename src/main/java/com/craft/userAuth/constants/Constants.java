package com.craft.userAuth.constants;

public class Constants {
    public static class ErrorMessage{
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String RECOVERY_CODE_REQUIRED = "Recovery code is required";
        public static final String FIRST_NAME_REQUIRED = "First Name is required";
        public static final String INVALID_PASSWORD = "Password should match the constraints";
    }

    public static class RegExpression{
        public static final String PASSWORD = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&-+=()])(?=\\\\S+$).{8, 20}$";
    }
}

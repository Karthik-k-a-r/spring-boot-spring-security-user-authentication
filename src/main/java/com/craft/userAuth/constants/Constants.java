package com.craft.userAuth.constants;

public class Constants {
    public static class ErrorCodes {
        public static final String DUPLICATE_EMAIL_CODE = "DUPLICATE_EMAIL";
        public static final String EMAIL_SEND_FAILED_CODE = "EMAIL_SEND_FAILED";
        public static final String INVALID_TOKEN_CODE="INVALID_TOKEN";
        public static final String USER_NOT_FOUND_CODE="USER_NOT_FOUND";
        public static final String INTERNAL_SERVER_ERROR_CODE="INTERNAL_SERVER_ERROR";
    }
    public static class ErrorMessages {
        public static final String INTERNAL_SERVER_ERROR="Internal Server Error";
        public static final String EMAIL_REQUIRED = "Email is required";

        public static final String DUPLICATE_EMAIL = "Email already exist";
        public static final String PASSWORD_REQUIRED = "Password is required";
        public static final String RECOVERY_CODE_REQUIRED = "Recovery code is required";
        public static final String FIRST_NAME_REQUIRED = "First Name is required";
        public static final String INVALID_PASSWORD = "Password should match the constraints";

        public static final String EMAIL_SEND_FAILED = "Exception occurred when sending mail to {0}";
        public static final String INVALID_TOKEN="Token verification failed";
        public static final String USER_NOT_FOUND="User Not found with Email - {0}";
    }

    public static class Email{
        public static final String FROM_MAIL_ID = "upsccontentmaterials@gmail.com";
        public static final String ACTIVATE_ACCOUNT_SUBJECT = "Please Activate your Account";
        public static final String ACTIVATE_ACCOUNT_BODY = "Thank you for account creation, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/v1/user/account/verify/{0}";
        public static final String PASSWORD_RECOVERY_SUBJECT="Password Reset Recovery Code";

        public static final String PASSWORD_RECOVERY_BODY="Password reset recovery code, " +
                "please use below recovery code for password reset :  {0}";
    }

    public static class RegExpression{
        public static final String PASSWORD = "[a-z0-9A-Z]";
    }

    public static class SuccessMessages{
        public static final String USER_ACCOUNT_CREATED="Your account has been successfully created.Please verify Email by the link sent on your Email address.";
        public static final String USER_ACCOUNT_VERIFIED=" Your account has been successfully verified and activated.";
        public static final String RECOVERY_CODE_SENT ="Password reset recovery code is successfully sent to your registered email. Kindly check.";
        public static final String PASSWORD_CHANGED="Your password has been changed successfully";
    }

    public static class Pagination{
        public static final String DEFAULT_PAGE_NUMBER = "0";
        public  static final String DEFAULT_PAGE_SIZE = "10";
    }
}

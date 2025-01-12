package com.thacbao.codeSphere.constants;

public class CodeSphereConstants {
    public static final String UNAUTHORIZED = "Unauthorized";

    public static final String ACCESS_TOKEN = "accessToken";

    public static final String EMAIL_SENDER_ERROR = "Some thing went wrong with email server, please try again later or contact admin to fix this issue";

    public static final String PERMISSION_DENIED = "You don't have permission to access this resource";

    public static final class User{
        public static final String USER_NOT_FOUND = "User not exist";

        public static final String USER_NAME_EXISTS = "User is already exist";

        public static final String EMAIL_EXISTS = "Email is already exist";
    }

    public static final class Exercise{
        public static final String EXERCISE_NOT_FOUND = "Exercise not exist";
    }
}

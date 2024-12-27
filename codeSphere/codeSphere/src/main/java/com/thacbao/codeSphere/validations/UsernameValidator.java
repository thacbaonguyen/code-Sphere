package com.thacbao.codeSphere.validations;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {
    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {
        if(username == null || username.isBlank()){
            return false;
        }
        return username.matches("^[a-zA-Z0-9_]+$");
    }
}

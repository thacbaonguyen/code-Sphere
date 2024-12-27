package com.thacbao.codeSphere.validations;

import org.apache.tomcat.jni.Local;

import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class DobValidator implements javax.validation.ConstraintValidator<ValidDob, LocalDate> {
    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext constraintValidatorContext) {
        if(dob == null){
            return false;
        }
        return dob.isBefore(LocalDate.now().minusYears(12));
    }
}

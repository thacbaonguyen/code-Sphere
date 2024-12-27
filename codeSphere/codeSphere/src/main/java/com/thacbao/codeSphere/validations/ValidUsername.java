package com.thacbao.codeSphere.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = UsernameValidator.class)
public @interface ValidUsername {
    String message() default "Invalid username format";

    Class<?> [] groups() default {};

    Class<? extends Payload> [] payload() default {};
}

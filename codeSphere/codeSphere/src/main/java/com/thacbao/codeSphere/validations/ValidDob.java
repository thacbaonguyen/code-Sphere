package com.thacbao.codeSphere.validations;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DobValidator.class)
public @interface ValidDob {
    String message() default "User must be at least 16 years old";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

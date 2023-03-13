package com.example.demo.annotations;

import com.example.demo.validations.EmailValidation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailValidation.class)
@Documented
public @interface ValidEmail {
    String message() default "Неверно введён email";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

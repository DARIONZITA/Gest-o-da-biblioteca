package com.example.bibliotecaapi.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AgeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAge {
    String message() default "Idade inválida";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int min() default 16; // Idade mínima para cadastro
    int max() default 120; // Idade máxima razoável
}
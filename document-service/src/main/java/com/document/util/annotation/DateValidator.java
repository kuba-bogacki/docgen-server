package com.document.util.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.util.Objects.requireNonNull;

public class DateValidator implements ConstraintValidator<Date, String> {

    private String format;

    @Override
    public void initialize(Date constraintAnnotation) {
        this.format = constraintAnnotation.format();
    }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintValidatorContext) {
        try {
            requireNonNull(string);
            LocalDate.parse(string, DateTimeFormatter.ofPattern(this.format));
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}

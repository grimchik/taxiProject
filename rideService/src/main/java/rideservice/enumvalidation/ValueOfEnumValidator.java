package rideservice.enumvalidation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ValueOfEnumValidator implements ConstraintValidator<ValueOfEnum, String> {
    private String enumValues;
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(ValueOfEnum constraintAnnotation) {
        enumClass = constraintAnnotation.enumClass();
        enumValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .anyMatch(v -> v.equals(value));

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    "Value must be one of the following: " + enumValues
            ).addConstraintViolation();
        }

        return isValid;
    }
}
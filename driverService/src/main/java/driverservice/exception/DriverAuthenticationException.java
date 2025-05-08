package driverservice.exception;

import org.springframework.security.core.AuthenticationException;

public class DriverAuthenticationException extends AuthenticationException {
    public DriverAuthenticationException(String message) {
        super(message);
    }
}
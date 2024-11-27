package com.portfolio.friends.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthenticationDTO(
        @Size(min = 5, max = 10, message = "Username must be between 5 and 10 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username must contain only alphanumeric characters")
        String username,
        @Size(min = 8, max = 12, message = "Password must be between 8 and 12 characters")
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Password must be between 8 and 12 characters")
        String password) {
}

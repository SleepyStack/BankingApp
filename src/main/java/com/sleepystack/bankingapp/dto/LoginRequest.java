package com.sleepystack.bankingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Email is required")
    @NotNull(message = "Email cannot be null")
    private String email;

    @NotBlank(message = "Password is required")
    @NotNull(message = "Password cannot be null")
    private String password;
}

package com.sleepystack.bankingapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateUserRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
}

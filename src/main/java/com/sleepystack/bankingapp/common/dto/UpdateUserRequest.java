package com.sleepystack.bankingapp.common.dto;

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

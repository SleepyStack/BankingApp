package com.sleepystack.bankingapp.resource.enums;

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN;

    @Override
    public String toString() {
        return name();
    }
}

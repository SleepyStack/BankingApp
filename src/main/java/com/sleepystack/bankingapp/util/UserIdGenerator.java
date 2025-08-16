package com.sleepystack.bankingapp.util;

import java.util.UUID;

public class UserIdGenerator {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}

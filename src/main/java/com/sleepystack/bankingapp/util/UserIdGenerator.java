package com.sleepystack.bankingapp.util;
import com.sleepystack.bankingapp.model.User;

import java.util.UUID;

public class UserIdGenerator {
    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }
}

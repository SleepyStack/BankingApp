package com.sleepystack.bankingapp.utils;
import com.sleepystack.bankingapp.model.User;

import java.util.UUID;

public class IdGenerator {
    public static String generateUserId() {
        return UUID.randomUUID().toString();
    }

    public static String generateAccountId() {
        return UUID.randomUUID().toString();
    }

    public static String generatePublicId(User user) {
        return UUID.randomUUID().toString() + "-" + user.getId();
    }
}

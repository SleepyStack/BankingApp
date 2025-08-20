package com.sleepystack.bankingapp.common.util;

import java.util.UUID;

public class UUIDIdGenerator {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}

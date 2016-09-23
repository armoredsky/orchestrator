package com.sysco.uomorchestrator.core.service

class GeneratesOrderReferenceNumbers {
    private static final int MAX_EXCLUSIVE = 10_000_000

    private final Random random

    GeneratesOrderReferenceNumbers(Random random) {
        this.random = random
    }

    String next() {
        int nextInt = random.nextInt(MAX_EXCLUSIVE)
        String.valueOf(nextInt).padLeft(7, '0')
    }
}

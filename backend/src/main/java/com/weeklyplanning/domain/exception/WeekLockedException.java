package com.weeklyplanning.domain.exception;

public class WeekLockedException extends RuntimeException {
    public WeekLockedException() {
        super("Week is locked and cannot be modified");
    }
}

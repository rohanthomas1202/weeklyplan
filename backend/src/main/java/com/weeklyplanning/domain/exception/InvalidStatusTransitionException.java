package com.weeklyplanning.domain.exception;

public class InvalidStatusTransitionException extends RuntimeException {
    public InvalidStatusTransitionException(String currentStatus, String targetStatus) {
        super("Invalid status transition from " + currentStatus + " to " + targetStatus);
    }
}

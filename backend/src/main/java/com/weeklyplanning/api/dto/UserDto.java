package com.weeklyplanning.api.dto;

import com.weeklyplanning.domain.enums.UserRole;

public record UserDto(Long id, String name, String email, UserRole role, Long teamId) {}

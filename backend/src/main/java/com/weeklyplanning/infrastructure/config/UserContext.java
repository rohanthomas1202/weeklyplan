package com.weeklyplanning.infrastructure.config;

import com.weeklyplanning.domain.entity.AppUser;

public class UserContext {
    private static final ThreadLocal<AppUser> currentUser = new ThreadLocal<>();

    public static void set(AppUser user) { currentUser.set(user); }
    public static AppUser get() { return currentUser.get(); }
    public static void clear() { currentUser.remove(); }
}

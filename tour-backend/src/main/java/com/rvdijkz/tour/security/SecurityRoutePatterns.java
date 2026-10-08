package com.rvdijkz.tour.security;

public final class SecurityRoutePatterns {

    public static final String[] PUBLIC_ENDPOINTS = {
        "/health",
        "/actuator/health",
        "/actuator/info",
        "/editions/current",
        "/editions/*/riders",
        "/editions/*/standings",
        "/editions/*/standings/*"
    };

    public static final String[] PLAYER_ENDPOINTS = {
        "/editions/*/entries/**",
        "/editions/*/predictions/**"
    };

    public static final String[] ADMIN_ENDPOINTS = {
        "/admin/**"
    };

    private SecurityRoutePatterns() {
    }
}


package com.github.nderwin.error.handling.control;

import java.net.URI;

public enum ErrorRegistry {

    INSUFFICIENT_FUNDS("insufficient-funds", "Not enough credit"),
    ACCOUNT_LOCKED("account-locked", "Account is locked");
    
    private static final String BASE_URI = "/errors#";
    
    private final String key;
    
    private final String defaultTitle;

    private ErrorRegistry(final String key, final String defaultTitle) {
        this.key = key;
        this.defaultTitle = defaultTitle;
    }
    
    public URI getType() {
        return URI.create(BASE_URI + key);
    }

    public String getTitle() {
        return defaultTitle;
    }
    
}

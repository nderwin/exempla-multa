package com.github.nderwin.api.adapter.boundary;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class VersionContext {
    
    private String version;

    public String getVersion() {
        return version;
    }

    public void setVersion(final String version) {
        this.version = version;
    }
    
}

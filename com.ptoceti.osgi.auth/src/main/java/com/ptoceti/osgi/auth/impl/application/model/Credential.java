package com.ptoceti.osgi.auth.impl.application.model;

import com.fasterxml.jackson.annotation.JsonGetter;

public class Credential {
    private String token;

    @JsonGetter
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

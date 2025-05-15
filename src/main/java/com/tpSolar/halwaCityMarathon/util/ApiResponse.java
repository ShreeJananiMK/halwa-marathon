package com.tpSolar.halwaCityMarathon.util;

import org.springframework.http.HttpStatus;

public class ApiResponse {

    private HttpStatus status;
    private Object message;
    private Object token;
    private String role;

    public ApiResponse(HttpStatus status, Object message) {
        this.status = status;
        this.message = message;
    }

    public ApiResponse(HttpStatus status, Object message, Object token, String role) {
        this.status = status;
        this.message = message;
        this.token = token;
        this.role = role;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

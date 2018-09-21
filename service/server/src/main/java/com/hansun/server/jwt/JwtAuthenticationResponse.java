package com.hansun.server.jwt;

/**
 * @author yuanl2
 */
public class JwtAuthenticationResponse {
    private final String token;

    public JwtAuthenticationResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return this.token;
    }

}

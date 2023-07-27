package com.example.makanmaniabe.payload;

import java.util.List;
import java.util.UUID;

public class JwtResponse {
    private String token;
    private String type = "Bearer";
    private UUID id;
    private String namaLengkap;
    private String email;
    private String telepon;
    private List<String> roles;

    public JwtResponse(String accessToken, UUID id, String namaLengkap, String email, String telepon, List<String> roles) {
        this.token = accessToken;
        this.id = id;
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.telepon = telepon;
        this.roles = roles;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setnamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setTelepon(String telepon) {
        this.telepon = telepon;
    }

    public String getTelepon() {
        return telepon;
    }

    public List<String> getRoles() {
        return roles;
    }
}

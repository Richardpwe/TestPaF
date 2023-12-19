package com.group_d.paf_server.dto;

public class PlayerDTO {

    private Long id;
    private String name;
    private String password;
    private String image;
    private boolean isReady;
    private String token;

    // Konstruktoren
    public PlayerDTO() {
    }

    public PlayerDTO(Long id, String name, String image, boolean isReady) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.isReady = isReady;
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean getIsReady() {
        return isReady;
    }

    public void setIsReady(boolean isReady) {
        this.isReady = isReady;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}


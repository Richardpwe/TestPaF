package com.group_d.paf_server.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "player")

public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column
    private String image;

    @Column
    private boolean isReady;

    // JPA benötigt einen leeren Standardkonstruktor
    public Player() {
    }

    // Konstruktor mit Parametern
    public Player(String name, String password, String image, boolean isReady) {
        this.name = name;
        this.password = password;
        this.image = image;
        this.isReady = isReady;
    }

    public Player(String name, String password) {
        this.name = name;
        this.password = password;
    }

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
}

package com.group_d.paf_server.util;

public class PlayerIdRequest {
    private Long playerId;

    // Standardkonstruktor für die Deserialisierung
    public PlayerIdRequest() {
    }

    // Getter und Setter
    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }
}


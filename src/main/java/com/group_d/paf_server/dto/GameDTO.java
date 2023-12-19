package com.group_d.paf_server.dto;

import com.group_d.paf_server.entity.Game;

import java.util.Date;

public class GameDTO {

    private Long id;
    private Long player1Id;
    private Long player2Id;
    private Long winnerId;
    private Date timestamp;
    private boolean draw;

    // Konstruktoren
    public GameDTO() {
    }

    // Konstruktor mit Game-Entität
    public GameDTO(Game game) {
        if (game != null) {
            this.id = game.getId();
            this.player1Id = game.getPlayer1() != null ? game.getPlayer1().getId() : null;
            this.player2Id = game.getPlayer2() != null ? game.getPlayer2().getId() : null;
            this.winnerId = game.getWinner() != null ? game.getWinner().getId() : null;
            this.timestamp = game.getTimestamp();
            this.draw = game.isDraw();
        }
    }

    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(Long player1Id) {
        this.player1Id = player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(Long winnerId) {
        this.winnerId = winnerId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }
}


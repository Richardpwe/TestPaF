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
    private String[][] board;

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
            if(game.getBoard() != null) {
                this.board = game.getBoard().getFields();
            }
        }
    }

    public Long getId() {
        return id;
    }

    public Long getPlayer1Id() {
        return player1Id;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public Long getWinnerId() {
        return winnerId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isDraw() {
        return draw;
    }

    public String[][] getBoard() {
        return board;
    }
}


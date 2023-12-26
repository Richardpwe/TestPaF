package com.group_d.paf_server.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import java.util.Arrays;

@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Speichern vom Board als JSON-String in der Datenbank
    @Column(length = 1024) // Spalte muss groß genug sein
    private String fieldsJson;

    // Transient, damit JPA dieses Feld ignoriert, da es aus fieldsJson kommt
    @Transient
    private String[][] fields;

    @PostLoad
    private void postLoad() {
        // Konvertieren von JSON-Zeichenkette zurück in ein 2D-Array, nachdem Entity geladen wurde
        this.fields = convertJsonToFields(this.fieldsJson);
    }

    public Board() {
        // Initialisieren vom Board mit einem leeren 2D-Array 6x7
        this.fields = new String[6][7];
        for (String[] row : this.fields) {
            Arrays.fill(row, "leer");
        }
        // Konvertiere das leere 2D-Array sofort in JSON
        this.fieldsJson = convertFieldsToJson(this.fields);
    }

    public void placeToken(int column, Player player) {
        // Überprüfen, ob die Spalte im gültigen Bereich ist
        if (column < 0 || column >= this.fields[0].length) {
            throw new IllegalArgumentException("Spalte außerhalb des gültigen Bereichs");
        }

        // Suche die tiefste freie Position in der Spalte
        for (int row = this.fields.length - 1; row >= 0; row--) {
            if (this.fields[row][column].equals("leer")) {
                this.fields[row][column] = player.getName();
                break;
            }
        }

        // Konvertiere das aktualisierte 2D-Array zurück in JSON
        this.fieldsJson = convertFieldsToJson(this.fields);
    }

    public enum GameState {
        IN_PROGRESS, WIN, DRAW
    }

    public GameState checkGameState() {
        // Überprüfen auf Gewinn
        if (hasFourInARow()) {
            return GameState.WIN;
        }

        // Überprüfen auf Unentschieden
        if (isBoardFull()) {
            return GameState.DRAW;
        }

        // Das Spiel läuft noch
        return GameState.IN_PROGRESS;
    }

    private boolean hasFourInARow() {
        for (int row = 0; row < fields.length; row++) {
            for (int col = 0; col < fields[0].length; col++) {
                String token = fields[row][col];
                if (!token.equals("leer")) {
                    // Horizontal
                    if (col + 3 < fields[0].length &&
                            token.equals(fields[row][col + 1]) &&
                            token.equals(fields[row][col + 2]) &&
                            token.equals(fields[row][col + 3])) {
                        return true;
                    }

                    // Vertikal
                    if (row + 3 < fields.length &&
                            token.equals(fields[row + 1][col]) &&
                            token.equals(fields[row + 2][col]) &&
                            token.equals(fields[row + 3][col])) {
                        return true;
                    }

                    // Diagonal nach unten rechts
                    if (row + 3 < fields.length && col + 3 < fields[0].length &&
                            token.equals(fields[row + 1][col + 1]) &&
                            token.equals(fields[row + 2][col + 2]) &&
                            token.equals(fields[row + 3][col + 3])) {
                        return true;
                    }

                    // Diagonal nach oben rechts
                    if (row - 3 >= 0 && col + 3 < fields[0].length &&
                            token.equals(fields[row - 1][col + 1]) &&
                            token.equals(fields[row - 2][col + 2]) &&
                            token.equals(fields[row - 3][col + 3])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private boolean isBoardFull() {
        for (int row = 0; row < fields.length; row++) {
            for (int col = 0; col < fields[row].length; col++) {
                if (fields[row][col].equals("leer")) {
                    return false;
                }
            }
        }
        return true;
    }


    // Getter und Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String[][] getFields() {
        return fields;
    }

    public void setFields(String[][] fields) {
        this.fields = fields;
        this.fieldsJson = convertFieldsToJson(fields);
    }

    // Hilfsmethoden zum Konvertieren von und zu JSON
    private String convertFieldsToJson(String[][] fields) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(fields);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting fields to JSON", e);
        }
    }

    private String[][] convertJsonToFields(String fieldsJson) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(fieldsJson, String[][].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting JSON to fields", e);
        }
    }
}

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

    public boolean isBoardFull() {
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
        return this.fields;
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

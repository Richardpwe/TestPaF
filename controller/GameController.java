package com.group_d.paf_server.controller;

import com.group_d.paf_server.entity.Board;
import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller-Klasse für Spieloperationen.
 * Diese Klasse handhabt HTTP-Anfragen, die sich auf Spiele beziehen,
 * wie das Erstellen, Aktualisieren, Löschen und Abrufen von Spieldetails.
 */
@RestController
@RequestMapping("game")
public class GameController {

    private final GameService gameService;

    /**
     * Konstruktor für GameController.
     *
     * @param gameService Der Service, der für die logik der Spieloperationen zuständig ist.
     */
    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Erstellt ein neues Spiel und speichert es in der Datenbank.
     *
     * @param game Das Spielobjekt, das erstellt werden soll.
     * @return Eine ResponseEntity, die das neu erstellte Spiel enthält.
     */
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game newGame = gameService.createGame(game);
        return ResponseEntity.ok(newGame);
    }

    /**
     * Ruft ein Spiel anhand seiner eindeutigen ID ab.
     *
     * @param id Die ID des Spiels, das abgerufen werden soll.
     * @return Eine ResponseEntity, die das angeforderte Spiel enthält.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = gameService.findGameById(id);
        return ResponseEntity.ok(game);
    }

    /**
     * Löscht ein Spiel anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Spiels.
     * @return Eine ResponseEntity ohne Inhalt, die den Erfolg der Operation anzeigt.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Ruft eine Liste aller Spiele ab.
     *
     * @return Eine ResponseEntity, die eine Liste aller Spiele enthält.
     */
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.findAllGames();
        return ResponseEntity.ok(games);
    }
}
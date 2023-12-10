package com.group_d.paf_server.controller;

import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("game")
public class GameController {

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Erstellt ein neues Spiel
    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game newGame = gameService.createGame(game);
        return ResponseEntity.ok(newGame);
    }

    // Ruft ein Spiel anhand seiner ID ab
    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = gameService.findGameById(id);
        return ResponseEntity.ok(game);
    }

    // Aktualisiert die Details eines Spiels
    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody Game gameDetails) {
        Game updatedGame = gameService.updateGame(id, gameDetails);
        return ResponseEntity.ok(updatedGame);
    }

    // Löscht ein Spiel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok().build();
    }

    // Liste aller Spiele
    @GetMapping
    public ResponseEntity<List<Game>> getAllGames() {
        List<Game> games = gameService.findAllGames();
        return ResponseEntity.ok(games);
    }
}

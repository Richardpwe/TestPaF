package com.group_d.paf_server.service;

import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlayerService playerService;

    @Autowired
    public GameService(GameRepository gameRepository, PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.playerService = playerService;
    }

    public Game createGame(Game game) {
        return gameRepository.save(game);
    }

    public Game startGame(Game newGame) {
        // TODO Spieler zuweisen, Spielstatus initialisieren
        newGame.setTimestamp(new Date());
        return gameRepository.save(newGame);
    }

    public Game endGame(Long gameId, Long winnerId) {
        Game game = findGameById(gameId);
        if (game != null) {
            if (winnerId != 0){
                game.setWinner(playerService.findPlayerById(winnerId));
                game.setDraw(false);
            }
            else{
                game.setDraw(true);
            }
            game.setTimestamp(new Date());
            return gameRepository.save(game);
        }
        throw new RuntimeException("Spiel mit ID " + gameId + " nicht gefunden.");
    }

    public String checkGameStatus(Long gameId) {
        Game game = findGameById(gameId);
        if (game != null) {
            if (game.getWinner() != null) {
                return "Gewonnen von " + game.getWinner().getName();
            }
            if (game.isDraw()) {
                return "Unentschieden";
            }
            return "Spiel läuft noch";
        }
        return "Spiel nicht gefunden";
    }


    public Game findGameById(Long id) {
        Optional<Game> game = gameRepository.findById(id);
        if (game.isPresent()) {
            return game.get();
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
            // throw new GameNotFoundException("Spiel mit ID " + id + " nicht gefunden.");
            return null;
        }
    }

    public Game updateGame(Long id, Game gameDetails) {
        Game game = findGameById(id);
        if (game != null) {
            // Aktualisieren der Attribute des Spiels
            game.setPlayer1(gameDetails.getPlayer1());
            game.setPlayer2(gameDetails.getPlayer2());
            game.setWinner(gameDetails.getWinner());
            game.setTimestamp(gameDetails.getTimestamp());
            game.setDraw(gameDetails.isDraw());
            return gameRepository.save(game);
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
            return null;
        }
    }

    public void deleteGame(Long id) {
        Game game = findGameById(id);
        if (game != null) {
            gameRepository.delete(game);
        } else {
            // TODO Exception, wenn das Spiel nicht gefunden wird
        }
    }

    public List<Game> findAllGames() {
        return gameRepository.findAll();
    }
}

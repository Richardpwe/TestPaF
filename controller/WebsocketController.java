package com.group_d.paf_server.controller;

import com.group_d.paf_server.dto.GameDTO;
import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.service.GameService;
import com.group_d.paf_server.service.PlayerService;
import com.group_d.paf_server.util.PlayerIdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "http://localhost:3000, http://localhost:5173/")
@Controller
public class WebsocketController {

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/findMatch")
    public void findMatch(PlayerIdRequest request) {
        Long playerId = request.getPlayerId();
        Long gameId = gameService.findMatch(playerId);
        String playerName = playerService.findPlayerById(playerId).getName();
        System.out.println(playerName);

        if (gameId != null) {
            // Match gefunden, Benachrichtigen der beteiligten Spieler
            Game game = gameService.findGameById(gameId);
            GameDTO gameDTO = new GameDTO(game);
            messagingTemplate.convertAndSendToUser(game.getPlayer1().getName(), "/topic/queue/matchFound", gameDTO);
            messagingTemplate.convertAndSendToUser(game.getPlayer2().getName(), "/topic/queue/matchFound", gameDTO);
        } else {
            // Kein Match gefunden, Spieler in die Warteschlange gestellt
            messagingTemplate.convertAndSend("/user/queue/waitingForMatch", "Warten auf einen Gegner...");
        }
    }


    // Empfängt Spieleraktionen und sendet Spielupdates
    @MessageMapping("/playerMove")
    public void playerMove(Long gameId, int column) {
        Game updatedGame = gameService.makeMove(gameId, column, gameService.getCurrentPlayer(gameId));
        GameDTO gameDTO = new GameDTO(updatedGame);

        // Senden des Spielupdates an alle Clients
        messagingTemplate.convertAndSend("/topic/gameUpdate", gameDTO);

        // Überprüfen, ob das Spiel beendet ist
        if (updatedGame.getWinner() != null || updatedGame.isDraw()) {
            // Senden einer Nachricht an beide Spieler, dass das Spiel beendet ist
            messagingTemplate.convertAndSendToUser(updatedGame.getPlayer1().getName(), "/queue/gameOver", gameDTO);
            messagingTemplate.convertAndSendToUser(updatedGame.getPlayer2().getName(), "/queue/gameOver", gameDTO);
        }
    }

    //Sendet den Spieler, der an der Reihe ist
    @MessageMapping("/currentPlayer")
    public void currentPlayer(Long gameId) {
        Player player = gameService.getCurrentPlayer(gameId);
        if (player != null) {
            // Senden des aktuellen Spielers an "/topic/game/" + gameId + "/currentPlayer"
            messagingTemplate.convertAndSend("/topic/game/" + gameId + "/currentPlayer", player);
        }
    }

    // Sendet Informationen über das Spielende an alle Clients
    @MessageMapping("/spielEnde")
    @SendTo("/topic/spielEnde")
    public GameDTO handleSpielEnde(Long gameId) {
        Game game = gameService.findGameById(gameId);
        return new GameDTO(game);
    }
}


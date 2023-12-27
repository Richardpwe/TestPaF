package com.group_d.paf_server.controller;

import com.group_d.paf_server.dto.GameDTO;
import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @Autowired
    private GameService gameService;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/findMatch")
    public void findMatch(String playerId, SimpMessageSendingOperations messagingTemplate) {
        Long gameId = gameService.findMatch(Long.parseLong(playerId));

        if (gameId != null) {
            Game game = gameService.findGameById(gameId);
            GameDTO gameDTO = new GameDTO(game);

            // Benachrichtigen Sie nur die beteiligten Spieler
            messagingTemplate.convertAndSendToUser(game.getPlayer1().getName(), "/queue/matchFound", gameDTO);
            messagingTemplate.convertAndSendToUser(game.getPlayer2().getName(), "/queue/matchFound", gameDTO);
        }
    }

    // Empfängt Spieleraktionen und sendet Spielupdates
    @MessageMapping("/playerMove")
    public void playerMove(Long gameId, int column, SimpMessagingTemplate template) {
        Game updatedGame = gameService.makeMove(gameId, column, gameService.getCurrentPlayer(gameId));
        GameDTO gameDTO = new GameDTO(updatedGame);

        // Senden des Spielupdates an alle Clients
        template.convertAndSend("/topic/gameUpdate", gameDTO);

        // Überprüfen, ob das Spiel beendet ist
        if (updatedGame.getGameState() == Game.GameState.WIN || updatedGame.getGameState() == Game.GameState.DRAW) {
            // Senden einer Nachricht an beide Spieler, dass das Spiel beendet ist
            template.convertAndSendToUser(updatedGame.getPlayer1().getName(), "/queue/gameOver", gameDTO);
            template.convertAndSendToUser(updatedGame.getPlayer2().getName(), "/queue/gameOver", gameDTO);
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


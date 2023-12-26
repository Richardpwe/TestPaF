package com.group_d.paf_server.controller;

import com.group_d.paf_server.dto.GameDTO;
import com.group_d.paf_server.entity.Game;
import com.group_d.paf_server.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {

    @Autowired
    private GameService gameService;

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
    @SendTo("/topic/gameUpdate")
    public GameDTO playerMove(Long gameId, int column) {
        // Spieleraktion durch die Spiel-ID und die Spaltennummer
        Game updatedGame = gameService.makeMove(gameId, column, gameService.getCurrentPlayer(gameId));
        return new GameDTO(updatedGame);
    }

    // Sendet Informationen über das Spielende an alle Clients
    @MessageMapping("/spielEnde")
    @SendTo("/topic/spielEnde")
    public GameDTO handleSpielEnde(Long gameId) {
        Game game = gameService.findGameById(gameId);
        return new GameDTO(game);
    }
}


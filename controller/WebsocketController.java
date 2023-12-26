package com.group_d.paf_server.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebsocketController {
    // Eine Methode, die Spielzüge verarbeitet
    @MessageMapping("/move")
    @SendTo("/topic/gameUpdate")
    public GameUpdate handleMove(Move move) {
        // Verarbeitungslogik für den Spielzug
        // Rückgabe eines GameUpdate-Objekts, das an alle abonnierten Clients gesendet wird
    }
}

package com.group_d.paf_server.controller;

import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.group_d.paf_server.dto.PlayerDTO;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("player")
public class PlayerController {
    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // POST-Endpoint zur Registrierung eines neuen Spielers
    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<PlayerDTO> registerPlayer(@RequestBody PlayerDTO playerDTO) {
        // Passwort nicht null oder leer
        if (playerDTO.getPassword() == null || playerDTO.getPassword().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        playerService.registerNewPlayer(playerDTO);

        // das Passwort löschen, bevor das DTO zurückgeben wird
        playerDTO.setPassword(null);
        return new ResponseEntity<>(playerDTO, HttpStatus.CREATED);
    }

    // GET-Endpoint zum Abrufen der Details eines Spielers anhand der ID

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        Player player = playerService.findPlayerById(id);
        PlayerDTO playerDTO = convertToDTO(player);
        return ResponseEntity.ok(playerDTO);
    }


    @PostMapping("/login")
    public ResponseEntity<PlayerDTO> loginPlayer(@RequestBody PlayerDTO playerDTO) {
        return null;
    }

    // Hilfsmethode zur Umwandlung eines Player-Objekts in ein PlayerDTO
    private PlayerDTO convertToDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getName(), player.getImage(), player.getIsReady());
    }
}
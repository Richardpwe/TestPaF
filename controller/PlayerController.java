package com.group_d.paf_server.controller;

import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.group_d.paf_server.dto.PlayerDTO;
import com.group_d.paf_server.security.jwt.JwtUtils;

/**
 * Controller-Klasse für Spieleroperationen.
 * Diese Klasse handhabt HTTP-Anfragen, die sich auf Spieler beziehen,
 * wie Registrierung, Login und Abrufen von Spielerdetails.
 */
@CrossOrigin(origins = "http://localhost:3000, http://localhost:5173/")
@RestController
@RequestMapping("player")
public class PlayerController {
    private final PlayerService playerService;
    private final JwtUtils jwtUtils;

    public PlayerController(PlayerService playerService, JwtUtils jwtUtils) {
        this.playerService = playerService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Registriert einen neuen Spieler im System.
     *
     * @param playerDTO Das DTO-Objekt des Spielers, das die für die Registrierung notwendigen Informationen enthält.
     * @return Eine ResponseEntity, die den Status der Operation und das PlayerDTO (ohne Passwort) enthält.
     */

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

    /**
     * Ruft die Details eines Spielers anhand seiner ID ab.
     *
     * @param id Die eindeutige ID des Spielers.
     * @return Eine ResponseEntity, die den Status der Operation und das PlayerDTO enthält.
     */

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayer(@PathVariable Long id) {
        Player player = playerService.findPlayerById(id);
        PlayerDTO playerDTO = convertToDTO(player);
        return ResponseEntity.ok(playerDTO);
    }

    /**
     * Ermöglicht es einem Spieler, sich im System anzumelden.
     *
     * @param playerDTO Das DTO-Objekt des Spielers, das die Anmeldeinformationen enthält.
     * @return Eine ResponseEntity, die den Status der Operation und das PlayerDTO enthält.
     */
    @PostMapping("/login")
    public ResponseEntity<PlayerDTO> loginPlayer(@RequestBody PlayerDTO playerDTO) {
        Player player = playerService.authenticatePlayer(playerDTO.getName(), playerDTO.getPassword());
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String jwt = jwtUtils.generateJwtToken(new UsernamePasswordAuthenticationToken(
                player.getName(), player.getPassword()));

        PlayerDTO responsePlayerDTO = new PlayerDTO(player.getId(),
                player.getName(),
                player.getImage(),
                player.getIsReady());
        responsePlayerDTO.setToken(jwt);
        return ResponseEntity.ok(responsePlayerDTO);
    }

    // Hilfsmethode zur Umwandlung eines Player-Objekts in ein PlayerDTO
    private PlayerDTO convertToDTO(Player player) {
        return new PlayerDTO(player.getId(), player.getName(), player.getImage(), player.getIsReady());
    }
}
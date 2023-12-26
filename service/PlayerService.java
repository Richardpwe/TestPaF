package com.group_d.paf_server.service;

import com.group_d.paf_server.dto.PlayerDTO;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.exception.PlayerNotFoundException;
import com.group_d.paf_server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Service-Klasse für die Verwaltung von Spieleroperationen.
 * Diese Klasse bietet Methoden zur Registrierung, Authentifizierung und zum Abrufen von Spielerdaten.
 */
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Konstruktor für PlayerService.
     *
     * @param playerRepository Das Repository für Spieleroperationen.
     * @param passwordEncoder  Der Encoder für die Passwortverschlüsselung.
     */
    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registriert einen neuen Spieler im System.
     *
     * @param playerDTO Das Data Transfer Object des Spielers mit den Registrierungsinformationen.
     * @return Das neu erstellte Player-Objekt.
     */
    public Player registerNewPlayer(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setPassword(passwordEncoder.encode(playerDTO.getPassword()));
        player.setImage(playerDTO.getImage());
        player.setIsReady(playerDTO.getIsReady());

        player = playerRepository.save(player); // Speichern des neuen Spielers in der Datenbank
        return player; // Rückgabe des neu registrierten Player-Objekts
    }

    /**
     * Authentifiziert einen Spieler basierend auf dem Benutzernamen und Passwort.
     *
     * @param name     Der Benutzername des Spielers.
     * @param password Das Passwort des Spielers.
     * @return Das Player-Objekt, wenn die Authentifizierung erfolgreich ist, andernfalls null.
     */
    public Player authenticatePlayer(String name, String password) {
        Optional<Player> optionalPlayer = playerRepository.findByName(name);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            if (passwordEncoder.matches(password, player.getPassword())) {
                return player;
            }
        }
        return null;
    }

    /**
     * Findet einen Spieler anhand seiner ID.
     *
     * @param id Die eindeutige ID des Spielers.
     * @return Das gefundene Player-Objekt.
     * @throws PlayerNotFoundException wenn kein Spieler mit der angegebenen ID gefunden wird.
     */
    public Player findPlayerById(Long id) throws PlayerNotFoundException {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
    }

    public Player findPlayerByName(String name) throws PlayerNotFoundException {
        return playerRepository.findByName(name)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with name: " + name));
    }
}



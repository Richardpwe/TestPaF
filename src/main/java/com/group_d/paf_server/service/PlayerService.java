package com.group_d.paf_server.service;

import com.group_d.paf_server.dto.PlayerDTO;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.exception.PlayerNotFoundException;
import com.group_d.paf_server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PlayerService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player registerNewPlayer(PlayerDTO playerDTO) {
        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setPassword(passwordEncoder.encode(playerDTO.getPassword()));
        player.setImage(playerDTO.getImage());
        player.setIsReady(playerDTO.getIsReady());

        player = playerRepository.save(player); // Speichern des neuen Spielers in der Datenbank
        return player; // Rückgabe des neu registrierten Player-Objekts
    }

    public Player findPlayerById(Long id) throws PlayerNotFoundException {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerNotFoundException("Player not found with id: " + id));
    }
}



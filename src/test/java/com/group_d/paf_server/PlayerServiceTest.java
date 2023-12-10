package com.group_d.paf_server;

import com.group_d.paf_server.dto.PlayerDTO;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.exception.PlayerNotFoundException;
import com.group_d.paf_server.repository.PlayerRepository;
import com.group_d.paf_server.service.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PlayerService playerService;

    @Test
    void whenRegisterPlayer_thenSuccess() {
        PlayerDTO mockPlayerDTO = new PlayerDTO(null, "username", "password", false);
        Player mockPlayer = new Player(mockPlayerDTO.getName(), "encodedPassword");

        Mockito.when(passwordEncoder.encode(mockPlayerDTO.getPassword())).thenReturn("encodedPassword");
        Mockito.when(playerRepository.save(any(Player.class))).thenReturn(mockPlayer);

        Player result = playerService.registerNewPlayer(mockPlayerDTO);

        assertNotNull(result);
        assertEquals("username", result.getName());
        assertEquals("encodedPassword", result.getPassword()); // Verify that the password is encoded
    }

    @Test
    void testFindPlayerByIdExisting() {
        Long playerId = 1L;
        Player mockPlayer = new Player("username", "encodedPassword");
        Mockito.when(playerRepository.findById(playerId)).thenReturn(Optional.of(mockPlayer));

        Player result = playerService.findPlayerById(playerId);

        assertNotNull(result);
        assertEquals("username", result.getName());
    }

    @Test
    void testFindPlayerByIdNonExisting() {
        Long playerId = 2L;
        Mockito.when(playerRepository.findById(playerId)).thenReturn(Optional.empty());

        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.findPlayerById(playerId);
        });
    }

    @Test
    void testFindPlayerByIdNull() {
        assertThrows(PlayerNotFoundException.class, () -> {
            playerService.findPlayerById(null);
        });
    }
}

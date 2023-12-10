package com.group_d.paf_server;

import org.junit.jupiter.api.Test;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Test
    public void testCreatePlayer() {
        // neue Player-Instanz
        Player player = new Player();
        player.setName("Test Player");
        player.setPassword("somePassword");

        Player savedPlayer = playerRepository.save(player);

        // Überprüfen Sie, ob der Player gespeichert wurde
        assertNotNull(savedPlayer.getId()); // ID generiert?
        assertEquals("Test Player", savedPlayer.getName());
    }
}

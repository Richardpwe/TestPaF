package com.group_d.paf_server.repository;

import com.group_d.paf_server.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // Findet einen Spieler anhand des Benutzernamens
    Optional<Player> findByName(String name);

    // Überprüfen, ob ein Benutzername bereits existiert
    boolean existsByName(String name);

    // Findet alle Spieler, die bereit sind
    List<Player> findByIsReady(boolean isReady);

}

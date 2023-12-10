package com.group_d.paf_server.repository;

import com.group_d.paf_server.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // Findet alle Spiele eines bestimmten Spielers
    List<Game> findByPlayer1IdOrPlayer2Id(int player1_id, int player2_id);

    // Findet alle Spiele, die ein Spieler gewonnen hat
    List<Game> findByWinnerId(int winner_id);

    // Findet Spiele an einem bestimmten Datum
    List<Game> findByTimestampBetween(Date startDate, Date endDate);

    // Findet Spiele, die unentschieden geendet haben
    List<Game> findByDraw(boolean draw);
}


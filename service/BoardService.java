package com.group_d.paf_server.service;

import com.group_d.paf_server.entity.Board;
import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BoardService {

    @Autowired
    private BoardRepository boardRepository;

    @Transactional
    public void placeTokenAndSave(Long boardId, int column, Player player) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found"));

        board.placeToken(column, player);
        boardRepository.save(board); // Speichert die Änderungen in der Datenbank
    }
}

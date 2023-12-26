package com.group_d.paf_server.repository;

import com.group_d.paf_server.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {
}

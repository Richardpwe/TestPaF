package com.group_d.paf_server.service;

import com.group_d.paf_server.entity.Player;
import com.group_d.paf_server.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlayerDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    public PlayerDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Player player = playerRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Spieler nicht gefunden: " + username));

        return User.withUsername(player.getName())
                .password(player.getPassword())
                .authorities("USER")
                .build();
    }
}

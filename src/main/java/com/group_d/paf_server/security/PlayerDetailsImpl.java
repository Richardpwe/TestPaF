package com.group_d.paf_server.security;

import com.group_d.paf_server.entity.Player;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class PlayerDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String password;

    public PlayerDetailsImpl(Player player) {
        this.id = player.getId();
        this.username = player.getName();
        this.password = player.getPassword();
    }

    public static PlayerDetailsImpl build(Player player) {
        return new PlayerDetailsImpl(player);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

 //Standardimplementierungen
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

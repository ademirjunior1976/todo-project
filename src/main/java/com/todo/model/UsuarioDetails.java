package com.todo.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioDetails implements UserDetails {

    private final Usuario usuario;

    public UsuarioDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() { return usuario; }

    public String getNome() {
        return usuario.getNome();
    }

    @Override public String getUsername()  { return usuario.getUsername(); }
    @Override public String getPassword()  { return usuario.getPassword(); }
    @Override public boolean isEnabled()   { return usuario.isAtivo(); }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = usuario.isAdmin() ? "ROLE_ADMIN" : "ROLE_USER";
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
}
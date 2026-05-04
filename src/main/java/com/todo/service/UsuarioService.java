package com.todo.service;

import com.todo.model.Usuario;
import com.todo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;

    public List<Usuario> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    @Transactional
    public Usuario salvar(Usuario usuario, String senha) {
        if (usuario.getId() == null) {
            usuario.setPassword(senha);
        } else if (senha != null && !senha.isBlank()) {
            usuario.setPassword(senha);
        } else {
            usuario.setPassword(buscarPorId(usuario.getId()).getPassword());
        }
        return repository.save(usuario);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public void alternarAtivo(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setAtivo(!usuario.isAtivo());
        repository.save(usuario);
    }
}
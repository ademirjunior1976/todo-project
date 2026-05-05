package com.todo.service;

import com.todo.model.Usuario;
import com.todo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<Usuario> listar() {
        return repository.findAllByOrderByNomeAsc();
    }

    public Usuario buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + id));
    }

    @Transactional
    public Usuario salvar(Usuario usuarioForm, String senha) {
        if (usuarioForm.getId() == null) {
            usuarioForm.setPassword(passwordEncoder.encode(senha));
            return repository.save(usuarioForm);
        }
        Usuario existing = buscarPorId(usuarioForm.getId());
        existing.setNome(usuarioForm.getNome());
        existing.setTelefone(usuarioForm.getTelefone());
        existing.setEmail(usuarioForm.getEmail());
        existing.setAtivo(usuarioForm.isAtivo());
        if (senha != null && !senha.isBlank()) {
            existing.setPassword(passwordEncoder.encode(senha));
        }
        return existing;
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

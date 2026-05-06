package com.todo.service;

import com.todo.model.Usuario;
import com.todo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    // ── Recuperação de senha ──────────────────────────────────────────────────

    public Optional<Usuario> buscarPorLoginOuEmail(String login) {
        Optional<Usuario> opt = repository.findByUsername(login);
        if (opt.isEmpty() && login.contains("@")) {
            opt = repository.findByEmail(login);
        }
        return opt;
    }

    @Transactional
    public String gerarTokenReset(Usuario usuario) {
        String token = UUID.randomUUID().toString().replace("-", "");
        usuario.setTokenReset(token);
        usuario.setTokenExpiracao(LocalDateTime.now().plusHours(1));
        repository.save(usuario);
        return token;
    }

    public Optional<Usuario> buscarPorTokenValido(String token) {
        return repository.findByTokenReset(token)
                .filter(u -> u.getTokenExpiracao() != null
                          && u.getTokenExpiracao().isAfter(LocalDateTime.now()));
    }

    @Transactional
    public void redefinirSenha(String token, String novaSenha) {
        Usuario usuario = buscarPorTokenValido(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou expirado"));
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        usuario.setTokenReset(null);
        usuario.setTokenExpiracao(null);
        repository.save(usuario);
    }
}

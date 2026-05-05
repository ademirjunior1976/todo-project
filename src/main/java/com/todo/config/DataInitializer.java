package com.todo.config;

import com.todo.model.Usuario;
import com.todo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (repository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin"));
            admin.setNome("Administrador");
            admin.setAtivo(true);
            repository.save(admin);
            return;
        }

        repository.findByUsernameAndAtivoTrue("admin").ifPresent(admin -> {
            if (!passwordEncoder.matches("admin", admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode("admin"));
                repository.save(admin);
            }
        });
    }
}
package com.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_usuario")
    @SequenceGenerator(name = "seq_usuario", sequenceName = "seq_usuario", allocationSize = 1)
    @Column(name = "cd_usuario")
    private Long id;

    @NotBlank(message = "O usuário é obrigatório")
    @Size(max = 50, message = "Usuário deve ter no máximo 50 caracteres")
    @Column(name = "ds_username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "ds_password", nullable = false, length = 100)
    private String password;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "ds_nome", nullable = false, length = 100)
    private String nome;

    @Size(max = 30, message = "Telefone deve ter no máximo 30 caracteres")
    @Column(name = "ds_telefone", length = 30)
    private String telefone;

    @Email(message = "E-mail inválido")
    @Size(max = 100, message = "E-mail deve ter no máximo 100 caracteres")
    @Column(name = "ds_email", length = 100)
    private String email;

    @Column(name = "ds_token_reset", length = 100)
    private String tokenReset;

    @Column(name = "dt_token_expiracao")
    private LocalDateTime tokenExpiracao;

    @Column(name = "fl_ativo", nullable = false)
    private boolean ativo = true;

    @Column(name = "fl_admin", nullable = false)
    private boolean admin = false;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}
package com.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import com.todo.model.Usuario;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_tarefa")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tarefa")
    @SequenceGenerator(name = "seq_tarefa", sequenceName = "seq_tarefa", allocationSize = 1)
    @Column(name = "cd_tarefa")
    private Long id;

    @NotBlank(message = "O título da tarefa é obrigatório")
    @Size(max = 200, message = "Título deve ter no máximo 200 caracteres")
    @Column(name = "ds_tarefa", nullable = false, length = 200)
    private String tarefa;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    @Column(name = "ds_texto", length = 1000)
    private String descricao;

    @NotNull(message = "A data de início é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dt_inicio", nullable = false)
    private LocalDate dataInicio;

    @NotNull(message = "A data de término é obrigatória")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "dt_fim", nullable = false)
    private LocalDate dataTermino;

    @Column(name = "ds_importancia", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Importancia importancia = Importancia.NORMAL;

    @Column(name = "ds_status", nullable = false, length = 15)
    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
        if (this.importancia == null) this.importancia = Importancia.NORMAL;
        if (this.status == null)      this.status = Status.PENDENTE;
    }

    public enum Importancia {
        NORMAL, URGENTE
    }

    public enum Status {
        PENDENTE, ANDAMENTO, CONCLUIDA
    }
}

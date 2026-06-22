package com.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_lista_compras")
public class ListaCompras {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_lista_compras")
    @SequenceGenerator(name = "seq_lista_compras", sequenceName = "seq_lista_compras", allocationSize = 1)
    @Column(name = "cd_lista")
    private Long id;

    @NotBlank(message = "O nome da lista é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nm_lista", nullable = false, length = 100)
    private String nome;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cd_usuario")
    private Usuario usuario;

    @OneToMany(mappedBy = "lista", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("categoria ASC, comprado ASC, produto ASC")
    private List<ItemCompra> itens = new ArrayList<>();

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}

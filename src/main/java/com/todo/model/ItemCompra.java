package com.todo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "tb_item_compra")
public class ItemCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_item_compra")
    @SequenceGenerator(name = "seq_item_compra", sequenceName = "seq_item_compra", allocationSize = 1)
    @Column(name = "cd_item")
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nm_produto", nullable = false, length = 100)
    private String produto;

    @Column(name = "fl_comprado", nullable = false)
    private boolean comprado = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cd_lista", nullable = false)
    private ListaCompras lista;

    @Column(name = "dt_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }
}

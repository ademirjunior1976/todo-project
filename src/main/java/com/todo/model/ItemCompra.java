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

    public enum Unidade {
        UNIDADE, KG, GRAMA, LITRO, ML, PACOTE, CAIXA, DUZIA;

        public String getLabel() {
            return switch (this) {
                case UNIDADE -> "un";
                case KG      -> "kg";
                case GRAMA   -> "g";
                case LITRO   -> "L";
                case ML      -> "mL";
                case PACOTE  -> "pct";
                case CAIXA   -> "cx";
                case DUZIA   -> "dz";
            };
        }
    }

    public enum Categoria {
        HORTIFRUTI, CARNES, LATICINIOS, PADARIA, BEBIDAS, LIMPEZA, HIGIENE, OUTROS;

        public String getLabel() {
            return switch (this) {
                case HORTIFRUTI -> "Hortifruti";
                case CARNES     -> "Carnes";
                case LATICINIOS -> "Laticínios";
                case PADARIA    -> "Padaria";
                case BEBIDAS    -> "Bebidas";
                case LIMPEZA    -> "Limpeza";
                case HIGIENE    -> "Higiene";
                case OUTROS     -> "Outros";
            };
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_item_compra")
    @SequenceGenerator(name = "seq_item_compra", sequenceName = "seq_item_compra", allocationSize = 1)
    @Column(name = "cd_item")
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Column(name = "nm_produto", nullable = false, length = 100)
    private String produto;

    @Column(name = "qt_quantidade")
    private Double quantidade;

    @Column(name = "ds_unidade", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Unidade unidade = Unidade.UNIDADE;

    @Column(name = "ds_categoria", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Categoria categoria = Categoria.OUTROS;

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

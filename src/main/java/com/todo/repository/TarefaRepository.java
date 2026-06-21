package com.todo.repository;

import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

    // --- Admin: sem filtro de usuário ---

    @Query("""
        SELECT t FROM Tarefa t
        WHERE (:busca IS NULL OR LOWER(t.tarefa) LIKE LOWER(CONCAT('%', CAST(:busca AS string), '%')))
        ORDER BY
            CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END,
            t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorTitulo(@Param("busca") String busca, Pageable pageable);

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.status = :status
        ORDER BY
            CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END,
            t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorStatus(@Param("status") Tarefa.Status status, Pageable pageable);

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.importancia = :importancia
        ORDER BY t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorImportancia(@Param("importancia") Tarefa.Importancia importancia, Pageable pageable);

    long countByStatus(Tarefa.Status status);
    long countByImportancia(Tarefa.Importancia importancia);

    // --- Usuário comum: filtrado por dono ---

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.usuario = :usuario
          AND (:busca IS NULL OR LOWER(t.tarefa) LIKE LOWER(CONCAT('%', CAST(:busca AS string), '%')))
        ORDER BY
            CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END,
            t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorTituloEUsuario(@Param("busca") String busca, @Param("usuario") Usuario usuario, Pageable pageable);

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.usuario = :usuario
          AND t.status = :status
        ORDER BY
            CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END,
            t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorStatusEUsuario(@Param("status") Tarefa.Status status, @Param("usuario") Usuario usuario, Pageable pageable);

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.usuario = :usuario
          AND t.importancia = :importancia
        ORDER BY t.dataTermino ASC
        """)
    Page<Tarefa> buscarPorImportanciaEUsuario(@Param("importancia") Tarefa.Importancia importancia, @Param("usuario") Usuario usuario, Pageable pageable);

    long countByUsuario(Usuario usuario);
    long countByStatusAndUsuario(Tarefa.Status status, Usuario usuario);
    long countByImportanciaAndUsuario(Tarefa.Importancia importancia, Usuario usuario);

    // --- Lembrete: tarefas com prazo exatamente em 1 ou 3 dias ---

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.status <> :concluida
          AND (t.dataTermino = :tresDias OR t.dataTermino = :umDia)
        ORDER BY t.dataTermino ASC
        """)
    List<Tarefa> buscarProximasDoVencimento(
        @Param("concluida") Tarefa.Status concluida,
        @Param("tresDias")  LocalDate tresDias,
        @Param("umDia")     LocalDate umDia);

    // --- Relatório PDF: todos os filtros combinados + JOIN FETCH para evitar LazyInit ---

    @Query("""
        SELECT t FROM Tarefa t LEFT JOIN FETCH t.usuario
        WHERE (:busca IS NULL OR LOWER(t.tarefa) LIKE LOWER(CONCAT('%', CAST(:busca AS string), '%')))
          AND (:status IS NULL OR CAST(t.status AS string) = :status)
          AND (:importancia IS NULL OR CAST(t.importancia AS string) = :importancia)
          AND (:inicioFrom IS NULL OR t.dataInicio >= :inicioFrom)
          AND (:inicioTo   IS NULL OR t.dataInicio <= :inicioTo)
          AND (:fimFrom    IS NULL OR t.dataTermino >= :fimFrom)
          AND (:fimTo      IS NULL OR t.dataTermino <= :fimTo)
        ORDER BY CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END, t.dataTermino ASC
        """)
    List<Tarefa> relatorioAdmin(
        @Param("busca") String busca,
        @Param("status") String status,
        @Param("importancia") String importancia,
        @Param("inicioFrom") LocalDate inicioFrom,
        @Param("inicioTo")   LocalDate inicioTo,
        @Param("fimFrom")    LocalDate fimFrom,
        @Param("fimTo")      LocalDate fimTo);

    @Query("""
        SELECT t FROM Tarefa t
        WHERE t.usuario = :usuario
          AND (:busca IS NULL OR LOWER(t.tarefa) LIKE LOWER(CONCAT('%', CAST(:busca AS string), '%')))
          AND (:status IS NULL OR CAST(t.status AS string) = :status)
          AND (:importancia IS NULL OR CAST(t.importancia AS string) = :importancia)
          AND (:inicioFrom IS NULL OR t.dataInicio >= :inicioFrom)
          AND (:inicioTo   IS NULL OR t.dataInicio <= :inicioTo)
          AND (:fimFrom    IS NULL OR t.dataTermino >= :fimFrom)
          AND (:fimTo      IS NULL OR t.dataTermino <= :fimTo)
        ORDER BY CASE WHEN t.importancia = 'URGENTE' THEN 0 ELSE 1 END, t.dataTermino ASC
        """)
    List<Tarefa> relatorioUsuario(
        @Param("usuario") Usuario usuario,
        @Param("busca") String busca,
        @Param("status") String status,
        @Param("importancia") String importancia,
        @Param("inicioFrom") LocalDate inicioFrom,
        @Param("inicioTo")   LocalDate inicioTo,
        @Param("fimFrom")    LocalDate fimFrom,
        @Param("fimTo")      LocalDate fimTo);
}
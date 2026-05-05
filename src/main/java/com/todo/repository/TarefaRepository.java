package com.todo.repository;

import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
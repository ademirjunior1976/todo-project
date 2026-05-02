package com.todo.repository;

import com.todo.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TarefaRepository extends JpaRepository<Tarefa, Long> {

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
}

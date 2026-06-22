package com.todo.repository;

import com.todo.model.ListaCompras;
import com.todo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaComprasRepository extends JpaRepository<ListaCompras, Long> {

    List<ListaCompras> findByUsuarioOrderByDataCriacaoDesc(Usuario usuario);

    @Query("SELECT l FROM ListaCompras l ORDER BY l.dataCriacao DESC")
    List<ListaCompras> findAllOrderByDataCriacaoDesc();

    @Query("SELECT COUNT(i) FROM ItemCompra i WHERE i.lista.id = :listaId")
    long countItens(@Param("listaId") Long listaId);

    @Query("SELECT COUNT(i) FROM ItemCompra i WHERE i.lista.id = :listaId AND i.comprado = true")
    long countItensComprados(@Param("listaId") Long listaId);
}

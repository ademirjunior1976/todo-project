package com.todo.repository;

import com.todo.model.ItemCompra;
import com.todo.model.ListaCompras;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemCompraRepository extends JpaRepository<ItemCompra, Long> {

    List<ItemCompra> findByListaOrderByCompradoAscProdutoAsc(ListaCompras lista);
}

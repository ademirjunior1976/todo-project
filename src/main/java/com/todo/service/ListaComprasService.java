package com.todo.service;

import com.todo.model.ItemCompra;
import com.todo.model.ListaCompras;
import com.todo.model.Usuario;
import com.todo.repository.ItemCompraRepository;
import com.todo.repository.ListaComprasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ListaComprasService {

    private final ListaComprasRepository listaRepository;
    private final ItemCompraRepository itemRepository;

    public List<ListaCompras> listar(Usuario usuario) {
        return usuario.isAdmin()
                ? listaRepository.findAllOrderByDataCriacaoDesc()
                : listaRepository.findByUsuarioOrderByDataCriacaoDesc(usuario);
    }

    public ListaCompras buscarPorId(Long id) {
        return listaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lista não encontrada: " + id));
    }

    @Transactional
    public ListaCompras salvar(ListaCompras lista) {
        return listaRepository.save(lista);
    }

    @Transactional
    public void excluir(Long id) {
        listaRepository.deleteById(id);
    }

    public Map<String, Long> resumo(Long listaId) {
        Map<String, Long> r = new LinkedHashMap<>();
        r.put("total",     listaRepository.countItens(listaId));
        r.put("comprados", listaRepository.countItensComprados(listaId));
        return r;
    }

    public List<ItemCompra> listarItens(ListaCompras lista) {
        return itemRepository.findByListaOrderByCategoriaAscCompradoAscProdutoAsc(lista);
    }

    @Transactional
    public void adicionarItem(Long listaId, ItemCompra item) {
        ListaCompras lista = buscarPorId(listaId);
        item.setLista(lista);
        itemRepository.save(item);
    }

    @Transactional
    public void toggleComprado(Long listaId, Long itemId) {
        ItemCompra item = itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));
        item.setComprado(!item.isComprado());
    }

    @Transactional
    public void excluirItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Transactional
    public void atualizarItem(ItemCompra item) {
        ItemCompra existing = itemRepository.findById(item.getId())
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + item.getId()));
        existing.setProduto(item.getProduto());
        existing.setQuantidade(item.getQuantidade());
        existing.setUnidade(item.getUnidade());
        existing.setCategoria(item.getCategoria());
    }

    public ItemCompra buscarItemPorId(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item não encontrado: " + itemId));
    }
}

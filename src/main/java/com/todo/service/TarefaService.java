package com.todo.service;

import com.todo.model.Tarefa;
import com.todo.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository repository;
    private static final int TAMANHO_PAGINA = 10;

    public Page<Tarefa> listar(String busca, int pagina) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        String filtro = (busca == null || busca.isBlank()) ? null : busca.trim();
        return repository.buscarPorTitulo(filtro, pageable);
    }

    public Page<Tarefa> listarPorStatus(String status, int pagina) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        Tarefa.Status s = Tarefa.Status.valueOf(status.toUpperCase());
        return repository.buscarPorStatus(s, pageable);
    }

    public Page<Tarefa> listarPorImportancia(String importancia, int pagina) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        Tarefa.Importancia i = Tarefa.Importancia.valueOf(importancia.toUpperCase());
        return repository.buscarPorImportancia(i, pageable);
    }

    public Tarefa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + id));
    }

    @Transactional
    public Tarefa salvar(Tarefa tarefa) {
        return repository.save(tarefa);
    }

    @Transactional
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    @Transactional
    public Tarefa alternarStatus(Long id) {
        Tarefa tarefa = buscarPorId(id);
        Tarefa.Status novoStatus = switch (tarefa.getStatus()) {
            case PENDENTE  -> Tarefa.Status.ANDAMENTO;
            case ANDAMENTO -> Tarefa.Status.CONCLUIDA;
            case CONCLUIDA -> Tarefa.Status.PENDENTE;
        };
        tarefa.setStatus(novoStatus);
        return repository.save(tarefa);
    }

    public Map<String, Long> resumoDashboard() {
        Map<String, Long> dados = new HashMap<>();
        dados.put("total",     repository.count());
        dados.put("pendentes", repository.countByStatus(Tarefa.Status.PENDENTE));
        dados.put("andamento", repository.countByStatus(Tarefa.Status.ANDAMENTO));
        dados.put("concluidas",repository.countByStatus(Tarefa.Status.CONCLUIDA));
        dados.put("urgentes",  repository.countByImportancia(Tarefa.Importancia.URGENTE));
        return dados;
    }
}

package com.todo.service;

import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import com.todo.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TarefaService {

    private final TarefaRepository repository;
    private static final int TAMANHO_PAGINA = 10;

    public Page<Tarefa> listar(String busca, int pagina, Usuario usuario) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        String filtro = (busca == null || busca.isBlank()) ? null : busca.trim();
        if (usuario.isAdmin()) {
            return repository.buscarPorTitulo(filtro, pageable);
        }
        return repository.buscarPorTituloEUsuario(filtro, usuario, pageable);
    }

    public Page<Tarefa> listarPorStatus(String status, int pagina, Usuario usuario) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        Tarefa.Status s = Tarefa.Status.valueOf(status.toUpperCase());
        if (usuario.isAdmin()) {
            return repository.buscarPorStatus(s, pageable);
        }
        return repository.buscarPorStatusEUsuario(s, usuario, pageable);
    }

    public Page<Tarefa> listarPorImportancia(String importancia, int pagina, Usuario usuario) {
        Pageable pageable = PageRequest.of(pagina, TAMANHO_PAGINA);
        Tarefa.Importancia i = Tarefa.Importancia.valueOf(importancia.toUpperCase());
        if (usuario.isAdmin()) {
            return repository.buscarPorImportancia(i, pageable);
        }
        return repository.buscarPorImportanciaEUsuario(i, usuario, pageable);
    }

    public Tarefa buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + id));
    }

    @Transactional
    public Tarefa salvar(Tarefa tarefaForm) {
        if (tarefaForm.getId() != null) {
            Tarefa existing = repository.findById(tarefaForm.getId())
                    .orElseThrow(() -> new RuntimeException("Tarefa não encontrada: " + tarefaForm.getId()));
            existing.setTarefa(tarefaForm.getTarefa());
            existing.setDescricao(tarefaForm.getDescricao());
            existing.setDataInicio(tarefaForm.getDataInicio());
            existing.setDataTermino(tarefaForm.getDataTermino());
            existing.setImportancia(tarefaForm.getImportancia());
            existing.setStatus(tarefaForm.getStatus());
            return existing;
        }
        return repository.save(tarefaForm);
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

    @Transactional(readOnly = true)
    public List<Tarefa> listarParaRelatorio(
            String busca, String status, String importancia,
            LocalDate inicioFrom, LocalDate inicioTo,
            LocalDate fimFrom,    LocalDate fimTo,
            Usuario usuario) {
        String b  = (busca == null || busca.isBlank()) ? null : busca.trim();
        Tarefa.Status s = (status == null || status.isBlank()) ? null
                        : Tarefa.Status.valueOf(status.toUpperCase());
        Tarefa.Importancia i = (importancia == null || importancia.isBlank()) ? null
                             : Tarefa.Importancia.valueOf(importancia.toUpperCase());
        String sNome = s != null ? s.name() : null;
        String iNome = i != null ? i.name() : null;
        if (usuario.isAdmin()) {
            return repository.relatorioAdmin(b, sNome, iNome, inicioFrom, inicioTo, fimFrom, fimTo);
        }
        return repository.relatorioUsuario(usuario, b, sNome, iNome, inicioFrom, inicioTo, fimFrom, fimTo);
    }

    public Map<String, Long> resumoDashboard(Usuario usuario) {
        Map<String, Long> dados = new HashMap<>();
        if (usuario.isAdmin()) {
            dados.put("total",     repository.count());
            dados.put("pendentes", repository.countByStatus(Tarefa.Status.PENDENTE));
            dados.put("andamento", repository.countByStatus(Tarefa.Status.ANDAMENTO));
            dados.put("concluidas",repository.countByStatus(Tarefa.Status.CONCLUIDA));
            dados.put("urgentes",  repository.countByImportancia(Tarefa.Importancia.URGENTE));
        } else {
            dados.put("total",     repository.countByUsuario(usuario));
            dados.put("pendentes", repository.countByStatusAndUsuario(Tarefa.Status.PENDENTE, usuario));
            dados.put("andamento", repository.countByStatusAndUsuario(Tarefa.Status.ANDAMENTO, usuario));
            dados.put("concluidas",repository.countByStatusAndUsuario(Tarefa.Status.CONCLUIDA, usuario));
            dados.put("urgentes",  repository.countByImportanciaAndUsuario(Tarefa.Importancia.URGENTE, usuario));
        }
        return dados;
    }
}
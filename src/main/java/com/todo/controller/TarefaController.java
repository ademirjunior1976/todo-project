package com.todo.controller;

import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import com.todo.model.UsuarioDetails;
import com.todo.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TarefaController {

    private final TarefaService service;

    // -------------------------------------------------------
    // DASHBOARD
    // -------------------------------------------------------
    @GetMapping("/")
    public String dashboard(@AuthenticationPrincipal UsuarioDetails principal, Model model) {
        Map<String, Long> resumo = service.resumoDashboard(principal.getUsuario());
        model.addAttribute("total",     resumo.get("total"));
        model.addAttribute("pendentes", resumo.get("pendentes"));
        model.addAttribute("andamento", resumo.get("andamento"));
        model.addAttribute("concluidas",resumo.get("concluidas"));
        model.addAttribute("urgentes",  resumo.get("urgentes"));
        return "dashboard";
    }

    // -------------------------------------------------------
    // LISTAGEM GERAL (com busca por título)
    // -------------------------------------------------------
    @GetMapping("/tarefas")
    public String listar(
            @AuthenticationPrincipal UsuarioDetails principal,
            @RequestParam(defaultValue = "") String busca,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        Page<Tarefa> page = service.listar(busca, pagina, principal.getUsuario());
        model.addAttribute("tarefas",      page.getContent());
        model.addAttribute("paginaAtual",  page.getNumber());
        model.addAttribute("totalPaginas", page.getTotalPages());
        model.addAttribute("totalItens",   page.getTotalElements());
        model.addAttribute("busca",        busca);
        model.addAttribute("tituloLista",  "Gestão de Tarefas");
        model.addAttribute("filtroAtivo",  "");
        return "tarefas/lista";
    }

    // -------------------------------------------------------
    // LISTAGEM FILTRADA POR STATUS (vinda do dashboard)
    // -------------------------------------------------------
    @GetMapping("/tarefas/filtro/status/{status}")
    public String listarPorStatus(
            @AuthenticationPrincipal UsuarioDetails principal,
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        Page<Tarefa> page = service.listarPorStatus(status, pagina, principal.getUsuario());
        String titulo = switch (status.toUpperCase()) {
            case "PENDENTE"  -> "Tarefas Pendentes";
            case "ANDAMENTO" -> "Tarefas em Andamento";
            case "CONCLUIDA" -> "Tarefas Concluídas";
            default          -> "Tarefas";
        };
        model.addAttribute("tarefas",      page.getContent());
        model.addAttribute("paginaAtual",  page.getNumber());
        model.addAttribute("totalPaginas", page.getTotalPages());
        model.addAttribute("totalItens",   page.getTotalElements());
        model.addAttribute("busca",        "");
        model.addAttribute("tituloLista",  titulo);
        model.addAttribute("filtroAtivo",  "status/" + status);
        return "tarefas/lista";
    }

    // -------------------------------------------------------
    // LISTAGEM FILTRADA POR IMPORTÂNCIA (vinda do dashboard)
    // -------------------------------------------------------
    @GetMapping("/tarefas/filtro/importancia/{importancia}")
    public String listarPorImportancia(
            @AuthenticationPrincipal UsuarioDetails principal,
            @PathVariable String importancia,
            @RequestParam(defaultValue = "0") int pagina,
            Model model) {
        Page<Tarefa> page = service.listarPorImportancia(importancia, pagina, principal.getUsuario());
        model.addAttribute("tarefas",      page.getContent());
        model.addAttribute("paginaAtual",  page.getNumber());
        model.addAttribute("totalPaginas", page.getTotalPages());
        model.addAttribute("totalItens",   page.getTotalElements());
        model.addAttribute("busca",        "");
        model.addAttribute("tituloLista",  "Tarefas Urgentes");
        model.addAttribute("filtroAtivo",  "importancia/" + importancia);
        return "tarefas/lista";
    }

    // -------------------------------------------------------
    // NOVO
    // -------------------------------------------------------
    @GetMapping("/tarefas/novo")
    public String novo(Model model) {
        model.addAttribute("tarefaForm",   new Tarefa());
        model.addAttribute("importancias", Tarefa.Importancia.values());
        model.addAttribute("statusList",   Tarefa.Status.values());
        return "tarefas/form";
    }

    // -------------------------------------------------------
    // EDITAR
    // -------------------------------------------------------
    @GetMapping("/tarefas/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("tarefaForm",   service.buscarPorId(id));
        model.addAttribute("importancias", Tarefa.Importancia.values());
        model.addAttribute("statusList",   Tarefa.Status.values());
        return "tarefas/form";
    }

    // -------------------------------------------------------
    // SALVAR
    // -------------------------------------------------------
    @PostMapping("/tarefas/salvar")
    public String salvar(
            @AuthenticationPrincipal UsuarioDetails principal,
            @Valid @ModelAttribute("tarefaForm") Tarefa tarefa,
            BindingResult result,
            Model model,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("importancias", Tarefa.Importancia.values());
            model.addAttribute("statusList",   Tarefa.Status.values());
            return "tarefas/form";
        }
        if (tarefa.getId() == null) {
            tarefa.setUsuario(principal.getUsuario());
        }
        service.salvar(tarefa);
        redirect.addFlashAttribute("sucesso", "Tarefa salva com sucesso!");
        return "redirect:/tarefas";
    }

    // -------------------------------------------------------
    // EXCLUIR
    // -------------------------------------------------------
    @GetMapping("/tarefas/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("sucesso", "Tarefa excluída com sucesso!");
        return "redirect:/tarefas";
    }

    // -------------------------------------------------------
    // ALTERNAR STATUS
    // -------------------------------------------------------
    @GetMapping("/tarefas/status/{id}")
    public String alternarStatus(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarStatus(id);
        redirect.addFlashAttribute("sucesso", "Status atualizado!");
        return "redirect:/tarefas";
    }
}
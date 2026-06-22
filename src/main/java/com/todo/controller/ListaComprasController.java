package com.todo.controller;

import com.todo.model.ItemCompra;
import com.todo.model.ListaCompras;
import com.todo.model.UsuarioDetails;
import com.todo.service.ListaComprasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/compras")
public class ListaComprasController {

    private final ListaComprasService service;

    // -------------------------------------------------------
    // LISTAS
    // -------------------------------------------------------
    @GetMapping
    public String listas(@AuthenticationPrincipal UsuarioDetails principal, Model model) {
        List<ListaCompras> listas = service.listar(principal.getUsuario());
        Map<Long, Long> totais    = new java.util.LinkedHashMap<>();
        Map<Long, Long> comprados = new java.util.LinkedHashMap<>();
        for (ListaCompras l : listas) {
            Map<String, Long> r = service.resumo(l.getId());
            totais.put(l.getId(),    r.get("total"));
            comprados.put(l.getId(), r.get("comprados"));
        }
        model.addAttribute("listas",    listas);
        model.addAttribute("totais",    totais);
        model.addAttribute("comprados", comprados);
        model.addAttribute("novaLista", new ListaCompras());
        model.addAttribute("isAdmin",   principal.getUsuario().isAdmin());
        return "compras/listas";
    }

    @PostMapping
    public String salvarLista(
            @AuthenticationPrincipal UsuarioDetails principal,
            @Valid @ModelAttribute("novaLista") ListaCompras lista,
            BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            String msg = result.getFieldError("nome") != null
                    ? result.getFieldError("nome").getDefaultMessage()
                    : "Erro ao criar lista.";
            redirect.addFlashAttribute("erroLista", msg);
            return "redirect:/compras";
        }
        lista.setUsuario(principal.getUsuario());
        service.salvar(lista);
        redirect.addFlashAttribute("sucesso", "Lista criada com sucesso!");
        return "redirect:/compras";
    }

    @GetMapping("/{id}/excluir")
    public String excluirLista(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("sucesso", "Lista excluída com sucesso!");
        return "redirect:/compras";
    }

    // -------------------------------------------------------
    // ITENS
    // -------------------------------------------------------
    @GetMapping("/{id}")
    public String itens(@PathVariable Long id,
                        @AuthenticationPrincipal UsuarioDetails principal,
                        Model model) {
        ListaCompras lista = service.buscarPorId(id);
        Map<String, Long> resumo = service.resumo(id);
        model.addAttribute("lista",    lista);
        model.addAttribute("itens",    service.listarItens(lista));
        model.addAttribute("total",    resumo.get("total"));
        model.addAttribute("comprados",resumo.get("comprados"));
        model.addAttribute("novoItem", new ItemCompra());
        model.addAttribute("isAdmin",  principal.getUsuario().isAdmin());
        return "compras/itens";
    }

    @PostMapping("/{id}/itens")
    public String adicionarItem(
            @PathVariable Long id,
            @Valid @ModelAttribute("novoItem") ItemCompra item,
            BindingResult result,
            RedirectAttributes redirect) {
        if (result.hasErrors()) {
            redirect.addFlashAttribute("erro", "Preencha o nome do produto.");
            return "redirect:/compras/" + id;
        }
        service.adicionarItem(id, item);
        return "redirect:/compras/" + id;
    }

    @GetMapping("/{listaId}/itens/{itemId}/toggle")
    public String toggleComprado(@PathVariable Long listaId, @PathVariable Long itemId) {
        service.toggleComprado(listaId, itemId);
        return "redirect:/compras/" + listaId;
    }

    @GetMapping("/{listaId}/itens/{itemId}/excluir")
    public String excluirItem(@PathVariable Long listaId, @PathVariable Long itemId,
                              RedirectAttributes redirect) {
        service.excluirItem(itemId);
        redirect.addFlashAttribute("sucesso", "Item removido.");
        return "redirect:/compras/" + listaId;
    }
}

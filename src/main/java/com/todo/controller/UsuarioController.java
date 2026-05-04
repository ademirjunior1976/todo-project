package com.todo.controller;

import com.todo.model.Usuario;
import com.todo.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", service.listar());
        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("usuarioForm", new Usuario());
        return "usuarios/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("usuarioForm", service.buscarPorId(id));
        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(
            @Valid @ModelAttribute("usuarioForm") Usuario usuario,
            BindingResult result,
            @RequestParam(required = false) String senha,
            RedirectAttributes redirect) {
        if (usuario.getId() == null && (senha == null || senha.isBlank())) {
            result.rejectValue("password", "required", "A senha é obrigatória para novos usuários");
        }
        if (result.hasErrors()) {
            return "usuarios/form";
        }
        service.salvar(usuario, senha);
        redirect.addFlashAttribute("sucesso", "Usuário salvo com sucesso!");
        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id, RedirectAttributes redirect) {
        service.excluir(id);
        redirect.addFlashAttribute("sucesso", "Usuário excluído com sucesso!");
        return "redirect:/usuarios";
    }

    @GetMapping("/ativo/{id}")
    public String alternarAtivo(@PathVariable Long id, RedirectAttributes redirect) {
        service.alternarAtivo(id);
        redirect.addFlashAttribute("sucesso", "Status atualizado!");
        return "redirect:/usuarios";
    }
}
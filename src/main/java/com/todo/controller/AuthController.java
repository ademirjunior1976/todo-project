package com.todo.controller;

import com.todo.model.Usuario;
import com.todo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UsuarioService usuarioService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastroPage() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(
            @RequestParam String nome,
            @RequestParam String username,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam String senha,
            @RequestParam String confirmarSenha,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (!senha.equals(confirmarSenha)) {
            model.addAttribute("erro", "As senhas não conferem.");
            model.addAttribute("nome", nome);
            model.addAttribute("username", username);
            model.addAttribute("email", email);
            model.addAttribute("telefone", telefone);
            return "cadastro";
        }

        if (usuarioService.buscarPorLoginOuEmail(username).isPresent()) {
            model.addAttribute("erro", "Este login já está em uso. Escolha outro.");
            model.addAttribute("nome", nome);
            model.addAttribute("email", email);
            model.addAttribute("telefone", telefone);
            return "cadastro";
        }

        Usuario novo = new Usuario();
        novo.setNome(nome);
        novo.setUsername(username);
        novo.setEmail(email != null && !email.isBlank() ? email : null);
        novo.setTelefone(telefone != null && !telefone.isBlank() ? telefone : null);
        novo.setAtivo(true);
        novo.setAdmin(false);

        usuarioService.salvar(novo, senha);

        redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso! Faça login para continuar.");
        return "redirect:/login";
    }
}
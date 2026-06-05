package com.todo.controller;

import com.todo.model.Usuario;
import com.todo.model.UsuarioDetails;
import com.todo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioService usuarioService;

    @GetMapping
    public String perfil(Authentication authentication, Model model) {
        Usuario usuario = usuarioAtual(authentication);
        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping
    public String salvar(
            Authentication authentication,
            @RequestParam String nome,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) String senha,
            @RequestParam(required = false) String confirmarSenha,
            Model model,
            RedirectAttributes redirect) {

        Usuario usuario = usuarioAtual(authentication);

        if (senha != null && !senha.isBlank() && !senha.equals(confirmarSenha)) {
            usuario.setNome(nome);
            usuario.setEmail(email);
            usuario.setTelefone(telefone);
            model.addAttribute("erro", "As senhas não conferem.");
            model.addAttribute("usuario", usuario);
            return "perfil";
        }

        usuario.setNome(nome);
        usuario.setEmail(email != null && !email.isBlank() ? email : null);
        usuario.setTelefone(telefone != null && !telefone.isBlank() ? telefone : null);

        usuarioService.salvar(usuario, senha != null && !senha.isBlank() ? senha : null);

        redirect.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
        return "redirect:/perfil";
    }

    private Usuario usuarioAtual(Authentication authentication) {
        UsuarioDetails details = (UsuarioDetails) authentication.getPrincipal();
        return usuarioService.buscarPorId(details.getUsuario().getId());
    }
}

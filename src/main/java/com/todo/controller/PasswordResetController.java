package com.todo.controller;

import com.todo.model.Usuario;
import com.todo.service.EmailService;
import com.todo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recuperar-senha")
public class PasswordResetController {

    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @Value("${app.base-url}")
    private String baseUrl;

    @GetMapping
    public String formSolicitar() {
        return "recuperar-senha/solicitar";
    }

    @PostMapping
    public String solicitar(@RequestParam String login, Model model) {
        String loginTrimado = login.trim();

        Optional<Usuario> opt = usuarioService.buscarPorLoginOuEmail(loginTrimado);

        if (opt.isEmpty()) {
            model.addAttribute("erro",
                "Usuário não encontrado. Verifique o login informado ou solicite ao administrador.");
            model.addAttribute("login", loginTrimado);
            return "recuperar-senha/solicitar";
        }

        Usuario usuario = opt.get();

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            model.addAttribute("erro",
                "Este usuário não possui e-mail cadastrado. " +
                "Solicite ao administrador que registre um e-mail no seu cadastro.");
            model.addAttribute("login", loginTrimado);
            return "recuperar-senha/solicitar";
        }

        String token = usuarioService.gerarTokenReset(usuario);
        String link  = baseUrl + "/recuperar-senha/redefinir?token=" + token;

        try {
            emailService.enviarResetSenha(usuario.getEmail(), usuario.getNome(), link);
            model.addAttribute("enviado", true);
            model.addAttribute("emailMascarado", mascararEmail(usuario.getEmail()));
        } catch (Exception e) {
            model.addAttribute("erro",
                "Não foi possível enviar o e-mail. Tente novamente ou contate o administrador.");
        }

        return "recuperar-senha/solicitar";
    }

    @GetMapping("/redefinir")
    public String formRedefinir(@RequestParam String token, Model model) {
        if (usuarioService.buscarPorTokenValido(token).isEmpty()) {
            model.addAttribute("tokenInvalido", true);
            return "recuperar-senha/redefinir";
        }
        model.addAttribute("token", token);
        return "recuperar-senha/redefinir";
    }

    @PostMapping("/redefinir")
    public String redefinir(@RequestParam String token,
                            @RequestParam String novaSenha,
                            @RequestParam String confirmarSenha,
                            Model model,
                            RedirectAttributes redirect) {
        if (!novaSenha.equals(confirmarSenha)) {
            model.addAttribute("token", token);
            model.addAttribute("erro", "As senhas não coincidem.");
            return "recuperar-senha/redefinir";
        }
        if (novaSenha.length() < 6) {
            model.addAttribute("token", token);
            model.addAttribute("erro", "A senha deve ter pelo menos 6 caracteres.");
            return "recuperar-senha/redefinir";
        }
        try {
            usuarioService.redefinirSenha(token, novaSenha);
            redirect.addFlashAttribute("sucesso", "Senha redefinida com sucesso! Faça o login.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("tokenInvalido", true);
            return "recuperar-senha/redefinir";
        }
    }

    private String mascararEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 1) return email;
        String local  = email.substring(0, at);
        String domain = email.substring(at);
        return local.substring(0, Math.min(2, local.length())) + "***" + domain;
    }
}
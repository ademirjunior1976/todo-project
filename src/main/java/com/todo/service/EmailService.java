package com.todo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    public void enviarResetSenha(String destinatario, String nomeUsuario, String link) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(destinatario);
        msg.setSubject("Redefinição de senha - Bilca Systems");
        msg.setText(
            "Olá, " + nomeUsuario + "!\n\n" +
            "Recebemos uma solicitação para redefinir a senha da sua conta.\n\n" +
            "Clique no link abaixo para criar uma nova senha (válido por 1 hora):\n\n" +
            link + "\n\n" +
            "Se você não fez essa solicitação, ignore este e-mail. Sua senha permanecerá a mesma.\n\n" +
            "Bilca Systems"
        );
        mailSender.send(msg);
    }
}
package com.todo.service;

import com.todo.model.Tarefa;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public void enviarLembrete(String destinatario, String nomeUsuario, List<Tarefa> tarefas) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder corpo = new StringBuilder();
        corpo.append("Olá, ").append(nomeUsuario).append("!\n\n")
             .append("Você tem ").append(tarefas.size())
             .append(" tarefa(s) com prazo se aproximando:\n\n");

        for (Tarefa t : tarefas) {
            long dias = java.time.temporal.ChronoUnit.DAYS.between(
                java.time.LocalDate.now(), t.getDataTermino());
            corpo.append("• ").append(t.getTarefa())
                 .append("\n  Prazo: ").append(t.getDataTermino().format(fmt))
                 .append(" (").append(dias == 1 ? "amanhã" : "em " + dias + " dias").append(")")
                 .append(" | Status: ").append(t.getStatus().name())
                 .append(" | Importância: ").append(t.getImportancia().name())
                 .append("\n\n");
        }

        corpo.append("Acesse o sistema e atualize o status das suas tarefas.\n\nBilca Systems");

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(remetente);
        msg.setTo(destinatario);
        msg.setSubject("Lembrete de tarefas — Bilca Systems");
        msg.setText(corpo.toString());
        mailSender.send(msg);
    }
}
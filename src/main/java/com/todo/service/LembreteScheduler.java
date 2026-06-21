package com.todo.service;

import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import com.todo.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class LembreteScheduler {

    private final TarefaRepository tarefaRepository;
    private final EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *")
    public void enviarLembretes() {
        LocalDate hoje = LocalDate.now();

        List<Tarefa> tarefas = tarefaRepository.buscarProximasDoVencimento(
                Tarefa.Status.CONCLUIDA,
                hoje.plusDays(3),
                hoje.plusDays(1));

        Map<Usuario, List<Tarefa>> porUsuario = tarefas.stream()
                .filter(t -> t.getUsuario() != null
                        && t.getUsuario().getEmail() != null
                        && !t.getUsuario().getEmail().isBlank())
                .collect(Collectors.groupingBy(Tarefa::getUsuario));

        porUsuario.forEach((usuario, lista) -> {
            try {
                emailService.enviarLembrete(usuario.getEmail(), usuario.getNome(), lista);
                log.info("Lembrete enviado para {} ({} tarefa(s))", usuario.getEmail(), lista.size());
            } catch (Exception e) {
                log.warn("Falha ao enviar lembrete para {}: {}", usuario.getEmail(), e.getMessage());
            }
        });

        log.info("Job de lembretes concluído: {} usuário(s) notificado(s).", porUsuario.size());
    }
}

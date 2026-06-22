package com.todo.service;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.*;
import com.lowagie.text.pdf.draw.LineSeparator;
import com.lowagie.text.FontFactory;
import com.todo.model.ItemCompra;
import com.todo.model.ListaCompras;
import com.todo.model.Tarefa;
import com.todo.model.Usuario;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class RelatorioService {

    private static final Color AZUL      = new Color(0, 102, 204);
    private static final Color CINZA_ESC = new Color(30, 41, 59);
    private static final Color CINZA_CLR = new Color(148, 163, 184);
    private static final Color VERDE     = new Color(23, 168, 101);
    private static final Color LARANJA   = new Color(245, 158, 11);
    private static final Color VERMELHO  = new Color(224, 52, 52);
    private static final Color LINHA     = new Color(226, 232, 240);
    private static final Color ALT       = new Color(248, 250, 252);

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_DT   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    static {
        FontFactory.registerDirectories();
    }

    private static Font vf(float size, int style, Color color) {
        Font f = FontFactory.getFont("Verdana", size, style, color);
        if (f.getBaseFont() == null && f.getFamilyname().equalsIgnoreCase("unknown")) {
            return new Font(Font.HELVETICA, size, style, color);
        }
        return f;
    }

    public byte[] gerarListaCompras(ListaCompras lista, List<ItemCompra> itens, Usuario usuario) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 36, 36, 54, 44);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new Rodape());
            doc.open();

            // Cabeçalho
            long comprados = itens.stream().filter(ItemCompra::isComprado).count();
            Font fTit = vf(15, Font.BOLD,   AZUL);
            Font fSub = vf( 8, Font.NORMAL, CINZA_CLR);
            Paragraph titulo = new Paragraph("Lista de Compras — " + lista.getNome(), fTit);
            titulo.setSpacingAfter(3);
            doc.add(titulo);
            Paragraph sub = new Paragraph(
                "Gerado em: " + LocalDateTime.now().format(FMT_DT) +
                "   |   Usuário: " + usuario.getNome() +
                "   |   " + comprados + " de " + itens.size() + " iten(s) comprado(s)", fSub);
            sub.setSpacingAfter(4);
            doc.add(sub);
            doc.add(new Chunk(new LineSeparator(1f, 100f, AZUL, Element.ALIGN_CENTER, -2f)));
            doc.add(Chunk.NEWLINE);

            // Tabela
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{10, 74, 16});

            Font fH = vf(8, Font.BOLD, Color.WHITE);
            for (String h : new String[]{"", "Produto", "Qtd."}) {
                PdfPCell c = new PdfPCell(new Phrase(h, fH));
                c.setBackgroundColor(AZUL);
                c.setPadding(6);
                c.setBorder(Rectangle.NO_BORDER);
                c.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(c);
            }

            if (itens.isEmpty()) {
                PdfPCell vazio = new PdfPCell(
                    new Phrase("Nenhum item na lista.", vf(9, Font.ITALIC, CINZA_CLR)));
                vazio.setColspan(3);
                vazio.setPadding(12);
                vazio.setHorizontalAlignment(Element.ALIGN_CENTER);
                vazio.setBorder(Rectangle.NO_BORDER);
                table.addCell(vazio);
            }

            Font fDings = new Font(Font.ZAPFDINGBATS, 10, Font.BOLD);
            boolean alt = false;
            for (ItemCompra item : itens) {
                Color bg = alt ? ALT : Color.WHITE;
                Font fTexto = item.isComprado()
                        ? vf(9, Font.NORMAL, CINZA_CLR)
                        : vf(9, Font.NORMAL, CINZA_ESC);

                // Checkbox via ZapfDingbats: "4" = ✔, "o" = ❍
                fDings.setColor(item.isComprado() ? VERDE : CINZA_CLR);
                PdfPCell chk = new PdfPCell(new Phrase(item.isComprado() ? "4" : "o", fDings));
                chk.setPadding(5);
                chk.setBackgroundColor(bg);
                chk.setBorderColor(LINHA);
                chk.setBorderWidth(0.5f);
                chk.setBorder(Rectangle.BOTTOM);
                chk.setHorizontalAlignment(Element.ALIGN_CENTER);
                chk.setVerticalAlignment(Element.ALIGN_MIDDLE);
                table.addCell(chk);

                cel(table, item.getProduto(), fTexto, bg);

                String qtd = item.getQuantidade() != null ? item.getQuantidade() + "x" : "—";
                PdfPCell cQtd = new PdfPCell(new Phrase(qtd, fTexto));
                cQtd.setPadding(5);
                cQtd.setBackgroundColor(bg);
                cQtd.setBorderColor(LINHA);
                cQtd.setBorderWidth(0.5f);
                cQtd.setBorder(Rectangle.BOTTOM);
                cQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cQtd);

                alt = !alt;
            }
            doc.add(table);
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF da lista de compras", e);
        }
        return out.toByteArray();
    }

    public byte[] gerar(List<Tarefa> tarefas, Usuario usuario, Filtros filtros) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Rectangle pageSize = new Rectangle(PageSize.A4.getHeight(), PageSize.A4.getWidth());
        Document doc = new Document(pageSize, 36, 36, 54, 44);
        try {
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setPageEvent(new Rodape());
            doc.open();
            addCabecalho(doc, usuario, filtros, tarefas.size());
            addTabela(doc, tarefas, usuario.isAdmin());
            doc.close();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao gerar PDF", e);
        }
        return out.toByteArray();
    }

    private void addCabecalho(Document doc, Usuario usuario, Filtros filtros, int total)
            throws DocumentException {
        Font fTit  = vf(15, Font.BOLD,   AZUL);
        Font fSub  = vf( 8, Font.NORMAL, CINZA_CLR);
        Font fFilt = vf( 8, Font.ITALIC, CINZA_ESC);

        Paragraph titulo = new Paragraph("Relatório de Tarefas", fTit);
        titulo.setSpacingAfter(3);
        doc.add(titulo);

        Paragraph sub = new Paragraph(
            "Gerado em: " + LocalDateTime.now().format(FMT_DT) +
            "   |   Usuário: " + usuario.getNome() +
            "   |   Total: " + total + " tarefa(s)", fSub);
        sub.setSpacingAfter(4);
        doc.add(sub);

        String desc = filtros.descricao();
        if (!desc.isBlank()) {
            Paragraph pf = new Paragraph("Filtros: " + desc, fFilt);
            pf.setSpacingAfter(6);
            doc.add(pf);
        }

        doc.add(new Chunk(new LineSeparator(1f, 100f, AZUL, Element.ALIGN_CENTER, -2f)));
        doc.add(Chunk.NEWLINE);
    }

    private void addTabela(Document doc, List<Tarefa> tarefas, boolean isAdmin)
            throws DocumentException {
        int cols = isAdmin ? 7 : 6;
        PdfPTable table = new PdfPTable(cols);
        table.setWidthPercentage(100);
        if (isAdmin) {
            table.setWidths(new float[]{20, 20, 9, 9, 10, 10, 22});
        } else {
            table.setWidths(new float[]{26, 26, 11, 11, 13, 13});
        }

        Font fH = vf(8, Font.BOLD, Color.WHITE);
        String[] headers = isAdmin
            ? new String[]{"Tarefa", "Descrição", "Dt. Início", "Dt. Término", "Importância", "Status", "Cadastrado por"}
            : new String[]{"Tarefa", "Descrição", "Dt. Início", "Dt. Término", "Importância", "Status"};
        for (String h : headers) {
            PdfPCell c = new PdfPCell(new Phrase(h, fH));
            c.setBackgroundColor(AZUL);
            c.setPadding(6);
            c.setBorder(Rectangle.NO_BORDER);
            table.addCell(c);
        }

        if (tarefas.isEmpty()) {
            PdfPCell vazio = new PdfPCell(
                new Phrase("Nenhuma tarefa encontrada.", vf(9, Font.ITALIC, CINZA_CLR)));
            vazio.setColspan(cols);
            vazio.setPadding(12);
            vazio.setHorizontalAlignment(Element.ALIGN_CENTER);
            vazio.setBorder(Rectangle.NO_BORDER);
            table.addCell(vazio);
        }

        Font fNorm = vf(8, Font.NORMAL, CINZA_ESC);
        Font fBold = vf(8, Font.BOLD,   CINZA_ESC);
        boolean alt = false;

        for (Tarefa t : tarefas) {
            Color bg  = alt ? ALT : Color.WHITE;
            boolean u = t.getImportancia() == Tarefa.Importancia.URGENTE;

            cel(table, t.getTarefa(), u ? fBold : fNorm, bg);
            cel(table, t.getDescricao() != null ? t.getDescricao() : "—", fNorm, bg);
            cel(table, t.getDataInicio().format(FMT_DATA), fNorm, bg);
            cel(table, t.getDataTermino().format(FMT_DATA), fNorm, bg);

            cel(table, t.getImportancia().name(),
                vf(8, Font.BOLD, u ? VERMELHO : CINZA_CLR), bg);

            Color sc = switch (t.getStatus()) {
                case CONCLUIDA -> VERDE;
                case ANDAMENTO -> LARANJA;
                default        -> CINZA_CLR;
            };
            cel(table, t.getStatus().name(), vf(8, Font.BOLD, sc), bg);

            if (isAdmin) {
                cel(table, t.getUsuario() != null ? t.getUsuario().getNome() : "—", fNorm, bg);
            }
            alt = !alt;
        }
        doc.add(table);
    }

    private void cel(PdfPTable table, String texto, Font font, Color bg) {
        PdfPCell c = new PdfPCell(new Phrase(texto != null ? texto : "—", font));
        c.setPadding(5);
        c.setBackgroundColor(bg);
        c.setBorderColor(LINHA);
        c.setBorderWidth(0.5f);
        c.setBorder(Rectangle.BOTTOM);
        table.addCell(c);
    }

    private static class Rodape extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter w, Document d) {
            Font f = FontFactory.getFont("Verdana", 7, Font.NORMAL, new Color(148, 163, 184));
            PdfContentByte cb = w.getDirectContent();
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT,
                new Phrase("Bilca Systems • " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), f),
                d.leftMargin(), d.bottomMargin() - 16, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT,
                new Phrase("Página " + w.getPageNumber(), f),
                d.right(), d.bottomMargin() - 16, 0);
        }
    }

    public record Filtros(
        String busca,
        String status,
        String importancia,
        LocalDate inicioFrom,
        LocalDate inicioTo,
        LocalDate fimFrom,
        LocalDate fimTo
    ) {
        public String descricao() {
            StringBuilder sb = new StringBuilder();
            if (busca != null && !busca.isBlank())
                sb.append("Busca: \"").append(busca).append("\"  ");
            if (status != null && !status.isBlank())
                sb.append("Status: ").append(status).append("  ");
            if (importancia != null && !importancia.isBlank())
                sb.append("Importância: ").append(importancia).append("  ");
            if (inicioFrom != null)
                sb.append("Início de: ").append(inicioFrom.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("  ");
            if (inicioTo != null)
                sb.append("até: ").append(inicioTo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("  ");
            if (fimFrom != null)
                sb.append("Término de: ").append(fimFrom.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("  ");
            if (fimTo != null)
                sb.append("até: ").append(fimTo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("  ");
            return sb.toString().trim();
        }
    }
}

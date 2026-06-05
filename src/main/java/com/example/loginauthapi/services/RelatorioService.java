package com.example.loginauthapi.services;

import com.example.loginauthapi.model.Financeiro;
import com.example.loginauthapi.model.StatusLancamento;
import com.example.loginauthapi.model.TipoRelatorio;
import com.example.loginauthapi.repositories.FinanceiroRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final FinanceiroRepository financeiroRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public List<Financeiro> obterDadosFiltrados(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        List<Financeiro> todos = financeiroRepository.buscarParaRelatorio(inicio, fim);

        List<Financeiro> dadosFiltrados = todos.stream().filter(f -> {
            boolean isReceita = "RECEITA".equals(f.getConta().getTipo().name());
            boolean isDespesa = "DESPESA".equals(f.getConta().getTipo().name());

            return switch (tipo) {
                case RECEITAS_RECEBIDAS -> isReceita && (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL);
                case RECEITAS_PENDENTES -> isReceita && f.getStatus() == StatusLancamento.PENDENTE;
                case DESPESAS_PAGAS -> isDespesa && (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL);
                case DESPESAS_PENDENTES -> isDespesa && f.getStatus() == StatusLancamento.PENDENTE;
                case LANCAMENTOS_VENCIDOS -> f.getStatus() == StatusLancamento.VENCIDA;
                case EXTRATO_GERAL -> true;
            };
        }).collect(Collectors.toList());

        // NOVA VALIDAÇÃO: Se a lista estiver vazia, devolve 404 para o Angular
        if (dadosFiltrados.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum registro encontrado para este período.");
        }

        return dadosFiltrados;
    }

    // ================= GERADOR CSV =================
    public byte[] gerarCsv(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        List<Financeiro> dados = obterDadosFiltrados(tipo, inicio, fim);
        StringBuilder csv = new StringBuilder();

        // Padrão Excel PT-BR: Ponto e vírgula como separador
        csv.append("ID;Tipo;Conta;Descricao;Vencimento;Valor;Status\n");

        for (Financeiro f : dados) {
            csv.append(f.getId()).append(";")
                    .append(f.getConta().getTipo().name()).append(";")
                    .append(f.getConta().getNome()).append(";")
                    .append(f.getDescricao()).append(";")
                    .append(f.getDataVencimento() != null ? f.getDataVencimento().format(dateFormatter) : "").append(";")
                    .append(f.getValor()).append(";")
                    .append(f.getStatus().name()).append("\n");
        }

        // UTF-8 com BOM para o Excel ler acentos corretamente
        byte[] utf8bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] finalBytes = new byte[utf8bom.length + contentBytes.length];
        System.arraycopy(utf8bom, 0, finalBytes, 0, utf8bom.length);
        System.arraycopy(contentBytes, 0, finalBytes, utf8bom.length, contentBytes.length);

        return finalBytes;
    }

    // ================= GERADOR PDF =================
    public byte[] gerarPdf(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        List<Financeiro> dados = obterDadosFiltrados(tipo, inicio, fim);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4.rotate()); // Paisagem para caber colunas
        PdfWriter.getInstance(document, out);
        document.open();

        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
        Paragraph titulo = new Paragraph("MsSystems - Relatório Financeiro", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);

        Paragraph subtitulo = new Paragraph("Tipo: " + tipo.name().replace("_", " ") + " | Período: " + inicio + " a " + fim);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        document.add(subtitulo);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2f, 3f, 1.5f, 1.5f, 2f});

        adicionarCabecalhoPdf(table, "Tipo", "Conta", "Descrição", "Vencimento", "Valor", "Status");

        for (Financeiro f : dados) {
            table.addCell(f.getConta().getTipo().name());
            table.addCell(f.getConta().getNome());
            table.addCell(f.getDescricao());
            table.addCell(f.getDataVencimento() != null ? f.getDataVencimento().format(dateFormatter) : "");
            table.addCell("R$ " + f.getValor().toString());
            table.addCell(f.getStatus().name());
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void adicionarCabecalhoPdf(PdfPTable table, String... colunas) {
        Font fontCabecalho = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        for (String cabecalho : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(cabecalho, fontCabecalho));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final FinanceiroRepository financeiroRepository;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Cor primária do MsSystems (#5a7F78)
    private final Color primaryColor = new Color(90, 127, 120);

    public List<Financeiro> obterDadosFiltrados(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        List<Financeiro> todos = financeiroRepository.buscarParaRelatorio(inicio, fim);

        return todos.stream().filter(f -> {
            boolean isReceita = "RECEITA".equals(f.getConta().getTipo().name());
            boolean isDespesa = "DESPESA".equals(f.getConta().getTipo().name());

            return switch (tipo) {
                case TODAS_RECEITAS -> isReceita;
                case RECEITAS_RECEBIDAS -> isReceita && (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL);
                case RECEITAS_PENDENTES -> isReceita && f.getStatus() == StatusLancamento.PENDENTE;
                case RECEITAS_VENCIDAS -> isReceita && f.getStatus() == StatusLancamento.VENCIDA;

                case TODAS_DESPESAS -> isDespesa;
                case DESPESAS_PAGAS -> isDespesa && (f.getStatus() == StatusLancamento.PAGA || f.getStatus() == StatusLancamento.PAGAMENTO_PARCIAL);
                case DESPESAS_PENDENTES -> isDespesa && f.getStatus() == StatusLancamento.PENDENTE;
                case DESPESAS_VENCIDAS -> isDespesa && f.getStatus() == StatusLancamento.VENCIDA;

                case LANCAMENTOS_VENCIDOS -> f.getStatus() == StatusLancamento.VENCIDA;
                case EXTRATO_GERAL, RESUMO_SALDOS -> true;
            };
        }).collect(Collectors.toList());
    }

    public byte[] gerarCsv(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        List<Financeiro> dados = obterDadosFiltrados(tipo, inicio, fim);
        if (dados.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sem dados.");

        StringBuilder csv = new StringBuilder();
        csv.append("ID;Tipo;Conta;Descricao;Vencimento;Valor Original;Valor Pago;Status\n");

        for (Financeiro f : dados) {
            csv.append(f.getId()).append(";")
                    .append(f.getConta().getTipo().name()).append(";")
                    .append(f.getConta().getNome()).append(";")
                    .append(f.getDescricao()).append(";")
                    .append(f.getDataVencimento() != null ? f.getDataVencimento().format(dateFormatter) : "").append(";")
                    .append(f.getValor() != null ? f.getValor().toString().replace(".", ",") : "0,00").append(";")
                    .append(f.getValorPago() != null ? f.getValorPago().toString().replace(".", ",") : "0,00").append(";")
                    .append(f.getStatus().name()).append("\n");
        }

        byte[] utf8bom = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
        byte[] contentBytes = csv.toString().getBytes(StandardCharsets.UTF_8);
        byte[] finalBytes = new byte[utf8bom.length + contentBytes.length];
        System.arraycopy(utf8bom, 0, finalBytes, 0, utf8bom.length);
        System.arraycopy(contentBytes, 0, finalBytes, utf8bom.length, contentBytes.length);
        return finalBytes;
    }

    public byte[] gerarPdf(TipoRelatorio tipo, LocalDate inicio, LocalDate fim) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4.rotate(), 30, 30, 30, 30);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Cabeçalho Corporativo
            Paragraph titulo = new Paragraph("MsSystems", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, primaryColor));
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            Paragraph subtitulo = new Paragraph("Relatório: " + tipo.name().replace("_", " "), FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY));
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitulo);

            Paragraph periodo = new Paragraph("Período: " + inicio.format(dateFormatter) + " a " + fim.format(dateFormatter), FontFactory.getFont(FontFactory.HELVETICA, 11, Color.GRAY));
            periodo.setAlignment(Element.ALIGN_CENTER);
            periodo.setSpacingAfter(20);
            document.add(periodo);

            // Seleção de Relatório
            if (tipo == TipoRelatorio.RESUMO_SALDOS) {
                gerarRelatorioSaldos(document, inicio, fim);
            } else {
                gerarRelatorioPadrao(document, tipo, inicio, fim);
            }

            document.close();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao gerar PDF");
        }

        return out.toByteArray();
    }

    private void gerarRelatorioPadrao(Document document, TipoRelatorio tipo, LocalDate inicio, LocalDate fim) throws DocumentException {
        List<Financeiro> dados = obterDadosFiltrados(tipo, inicio, fim);
        if (dados.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum dado encontrado.");

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2.5f, 3.5f, 1.5f, 1.5f, 2f});
        adicionarCabecalhoPdf(table, "Tipo", "Conta", "Descrição", "Venc.", "Valor", "Status");

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        BigDecimal totalAcumulado = BigDecimal.ZERO;

        for (Financeiro f : dados) {
            BigDecimal val = f.getValorPago() != null ? f.getValorPago() : f.getValor();

            if (tipo == TipoRelatorio.EXTRATO_GERAL && "DESPESA".equals(f.getConta().getTipo().name())) {
                totalAcumulado = totalAcumulado.subtract(val);
            } else if (tipo == TipoRelatorio.EXTRATO_GERAL) {
                totalAcumulado = totalAcumulado.add(val);
            } else {
                totalAcumulado = totalAcumulado.add(val);
            }

            table.addCell(criarCelulaBasica(f.getConta().getTipo().name()));
            table.addCell(criarCelulaBasica(f.getConta().getNome()));
            table.addCell(criarCelulaBasica(f.getDescricao()));
            table.addCell(criarCelulaBasica(f.getDataVencimento() != null ? f.getDataVencimento().format(dateFormatter) : ""));
            table.addCell(criarCelulaBasica(nf.format(val)));
            table.addCell(criarCelulaBasica(f.getStatus().name().replace("_", " ")));
        }

        // Rodapé com Total
        PdfPCell cellTotal = new PdfPCell(new Phrase("TOTAL DO PERÍODO: " + nf.format(totalAcumulado), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        cellTotal.setColspan(6);
        cellTotal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellTotal.setBackgroundColor(new Color(240, 240, 240));
        cellTotal.setPadding(10);
        table.addCell(cellTotal);

        document.add(table);
    }

    private void gerarRelatorioSaldos(Document document, LocalDate inicio, LocalDate fim) throws DocumentException {
        List<Financeiro> todos = obterDadosFiltrados(TipoRelatorio.EXTRATO_GERAL, inicio, fim);

        BigDecimal tReceitas = todos.stream().filter(f -> "RECEITA".equals(f.getConta().getTipo().name()))
                .map(f -> f.getValorPago() != null ? f.getValorPago() : f.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tDespesas = todos.stream().filter(f -> "DESPESA".equals(f.getConta().getTipo().name()))
                .map(f -> f.getValorPago() != null ? f.getValorPago() : f.getValor()).reduce(BigDecimal.ZERO, BigDecimal::add);

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        adicionarCelulaResumo(table, "Total Receitas", tReceitas, new Color(45, 138, 78));
        adicionarCelulaResumo(table, "Total Despesas", tDespesas, new Color(239, 68, 68));
        adicionarCelulaResumo(table, "Saldo Líquido", tReceitas.subtract(tDespesas), primaryColor);

        document.add(table);
    }

    private void adicionarCelulaResumo(PdfPTable table, String label, BigDecimal val, Color cor) {
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        table.addCell(new PdfPCell(new Phrase(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12))));
        table.addCell(new PdfPCell(new Phrase(nf.format(val), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, cor))));
    }

    private void adicionarCabecalhoPdf(PdfPTable table, String... colunas) {
        for (String col : colunas) {
            PdfPCell cell = new PdfPCell(new Phrase(col, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
            cell.setBackgroundColor(primaryColor);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
    }

    private PdfPCell criarCelulaBasica(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        cell.setPadding(6);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }
}
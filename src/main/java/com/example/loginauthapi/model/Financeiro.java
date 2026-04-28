package com.example.loginauthapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "financeiro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Financeiro {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "conta_id", nullable = false)
    private Conta conta;

    private LocalDate dataEmissao;
    private LocalDate dataVencimento;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusLancamento status;

    private LocalDate dataPagamento;
    private BigDecimal valorPago;
    private String comprovanteUrl;
    private String descricao;

    // Método auxiliar para o Front-end identificar se é entrada ou saída
    public String getTipo() {
        return this.conta != null ? this.conta.getTipo().name() : null;
    }
}
package com.example.loginauthapi.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamentos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Lancamento {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

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

    // Getters e Setters
}
package com.example.loginauthapi.model;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceiroSpecification {

    public static Specification<Financeiro> comFiltros(String status, String tipo, Long contaId, LocalDate inicio, LocalDate fim) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro de Status[cite: 2]
            if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("TODOS")) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            // Filtro de Tipo (Receita/Despesa)[cite: 2]
            if (tipo != null && !tipo.trim().isEmpty() && !tipo.equalsIgnoreCase("TODOS")) {
                predicates.add(cb.equal(root.get("tipo"), tipo));
            }

            // Filtro de Conta Específica[cite: 2]
            if (contaId != null) {
                predicates.add(cb.equal(root.get("conta").get("id"), contaId));
            }

            // Filtro de Data (Período Personalizado ou Prefixado)[cite: 2]
            // Usamos a dataVencimento como base para buscar os lançamentos
            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataVencimento"), inicio));
            }
            if (fim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataVencimento"), fim));
            }

            // Ordenação padrão: mais recentes ou próximos de vencer primeiro
            query.orderBy(cb.asc(root.get("dataVencimento")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
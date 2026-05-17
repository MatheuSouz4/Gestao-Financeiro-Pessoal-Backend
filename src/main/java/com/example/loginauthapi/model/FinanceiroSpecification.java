package com.example.loginauthapi.model;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FinanceiroSpecification {

    public static Specification<Financeiro> comFiltros(
            StatusLancamento status,
            TipoConta tipo,
            Long contaId,
            LocalDate inicio,
            LocalDate fim) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (tipo != null) {
                // JOIN: Acessa o campo 'tipo' dentro da entidade 'Conta'
                Join<Financeiro, Conta> contaJoin = root.join("conta");
                predicates.add(cb.equal(contaJoin.get("tipo"), tipo));
            }

            if (contaId != null) {
                predicates.add(cb.equal(root.get("conta").get("id"), contaId));
            }

            if (inicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataVencimento"), inicio));
            }

            if (fim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataVencimento"), fim));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
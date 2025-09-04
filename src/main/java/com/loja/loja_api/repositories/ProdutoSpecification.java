package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import java.util.List;
import java.util.ArrayList;

public class ProdutoSpecification {

    public static Specification<Produto> comFiltros(
            List<String> categorias,
            List<String> marcas,
            List<String> objetivos,
            Double minPreco,
            Double maxPreco,
            Boolean destaque
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Adiciona a condição de 'ativo'
            predicates.add(criteriaBuilder.isTrue(root.get("ativo")));

            // Adiciona o filtro de 'preço'
            predicates.add(criteriaBuilder.between(
                    criteriaBuilder.coalesce(root.get("precoDesconto"), root.get("preco")),
                    minPreco,
                    maxPreco
            ));

            // Adiciona o filtro de 'destaque', se fornecido
            if (destaque != null) {
                predicates.add(criteriaBuilder.equal(root.get("destaque"), destaque));
            }

            // Adiciona o filtro de 'marcas', se fornecido
            if (marcas != null && !marcas.isEmpty()) {
                predicates.add(root.get("marca").in(marcas));
            }

            // Adiciona o filtro de 'categorias', se fornecido
            if (categorias != null && !categorias.isEmpty()) {
                Join<Produto, String> categoriasJoin = root.join("categorias");
                predicates.add(categoriasJoin.in(categorias));
            }

            // Adiciona o filtro de 'objetivos', se fornecido
            if (objetivos != null && !objetivos.isEmpty()) {
                Join<Produto, String> objetivosJoin = root.join("objetivos");
                predicates.add(objetivosJoin.in(objetivos));
            }

            // Agrupa todas as condições com 'AND'
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}

package com.loja.loja_api.services;

import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ValidadeService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> produtosComValidadeProxima(int dias) {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(dias);

        return produtoRepository.findAll().stream()
//                .filter(p -> p.getDataValidade() != null)
//                .filter(p -> !p.getDataValidade().isBefore(hoje))
//                .filter(p -> p.getDataValidade().isBefore(limite))
                .collect(Collectors.toList());
    }
}

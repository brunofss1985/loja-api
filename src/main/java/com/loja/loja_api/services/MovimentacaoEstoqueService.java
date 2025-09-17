package com.loja.loja_api.services;

import com.loja.loja_api.models.Lote;
import com.loja.loja_api.models.MovimentacaoEstoque;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.models.TipoMovimentacao;
import com.loja.loja_api.repositories.MovimentacaoEstoqueRepository;
import com.loja.loja_api.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;

@Service
public class MovimentacaoEstoqueService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private MovimentacaoEstoqueRepository movimentacaoRepository;

    @Transactional
    public void registrarMovimentacao(Long produtoId, TipoMovimentacao tipo, int quantidade, String loteCodigo) {
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        int estoqueTotal = produto.getLotes() != null
                ? produto.getLotes().stream().mapToInt(Lote::getQuantidade).sum()
                : 0;

        if (tipo == TipoMovimentacao.SAIDA && estoqueTotal < quantidade) {
            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        if (tipo == TipoMovimentacao.ENTRADA) {
            // Se for entrada, encontra o lote pelo código ou cria um novo
            Lote lote = produto.getLotes().stream()
                    .filter(l -> l.getCodigo().equals(loteCodigo))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Lote não encontrado: " + loteCodigo));

            lote.setQuantidade(lote.getQuantidade() + quantidade);
        } else {
            // Saída -> baixa usando FIFO (lotes com validade mais próxima primeiro)
            int restante = quantidade;

            for (Lote lote : produto.getLotes().stream()
                    .sorted(Comparator.comparing(Lote::getDataValidade))
                    .toList()) {

                if (restante <= 0) break;

                int retirar = Math.min(lote.getQuantidade(), restante);
                lote.setQuantidade(lote.getQuantidade() - retirar);
                restante -= retirar;
            }

            if (restante > 0) {
                throw new RuntimeException("Erro ao dar baixa: não foi possível retirar toda a quantidade.");
            }
        }

        produtoRepository.save(produto);

        movimentacaoRepository.save(MovimentacaoEstoque.builder()
                .produtoId(produtoId)
                .tipo(tipo)
                .quantidade(quantidade)
                .dataMovimentacao(LocalDate.now())
                .lote(loteCodigo)
                .build());

        if (produto.getEstoqueMinimo() != null && estoqueTotal <= produto.getEstoqueMinimo()) {
            System.out.println("⚠️ Estoque baixo para produto: " + produto.getNome());
        }
    }
}

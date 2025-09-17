package com.loja.loja_api.services;

import com.loja.loja_api.models.Lote;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import com.loja.loja_api.repositories.ProdutoSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class ProductChatService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public String getProductsForChatbot() {
        try {
            Specification<Produto> spec = ProdutoSpecification.comFiltros(null, null, null, null, null, null);
            List<Produto> produtos = produtoRepository.findAll(spec);

            if (produtos.isEmpty()) {
                return "PRODUTOS DISPONÍVEIS:\n- Nenhum produto ativo no momento. Entre em contato para mais informações.";
            }

            StringBuilder produtosInfo = new StringBuilder("PRODUTOS DISPONÍVEIS:\n");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (Produto produto : produtos) {
                Double precoFinal = produto.getPrecoDesconto() != null && produto.getPrecoDesconto() > 0
                        ? produto.getPrecoDesconto()
                        : produto.getPreco();

                String preco = precoFinal != null ?
                        currencyFormat.format(precoFinal) : "Consulte preço";

                String descricao = produto.getDescricaoCurta() != null ?
                        produto.getDescricaoCurta() :
                        (produto.getDescricao() != null ? produto.getDescricao() : "Suplemento de qualidade");

                String sabor = produto.getSabor() != null ? " (" + produto.getSabor() + ")" : "";

                produtosInfo.append(String.format(
                        "- %s%s: %s - %s\n",
                        produto.getNome(),
                        sabor,
                        preco,
                        descricao
                ));

                // ✅ Agora estoque é calculado a partir dos lotes
                int estoqueTotal = (produto.getLotes() != null)
                        ? produto.getLotes().stream().mapToInt(Lote::getQuantidade).sum()
                        : 0;

                if (produto.getEstoqueMinimo() != null && estoqueTotal <= produto.getEstoqueMinimo()) {
                    produtosInfo.append("  ⚠️ Estoque limitado!\n");
                }
            }

            return produtosInfo.toString();

        } catch (Exception e) {
            System.err.println("=== ERRO AO BUSCAR PRODUTOS: " + e.getMessage() + " ===");
            return "PRODUTOS DISPONÍVEIS:\n- Erro ao carregar produtos. Entre em contato para informações atualizadas.";
        }
    }

    public String getProductsByCategory(String categoria) {
        try {
            Specification<Produto> spec = ProdutoSpecification.comFiltros(List.of(categoria), null, null, null, null, null);
            List<Produto> produtos = produtoRepository.findAll(spec);

            if (produtos.isEmpty()) {
                return "Não encontramos produtos na categoria '" + categoria + "' no momento.";
            }

            StringBuilder result = new StringBuilder("PRODUTOS NA CATEGORIA " + categoria.toUpperCase() + ":\n");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (Produto produto : produtos) {
                Double precoFinal = produto.getPrecoDesconto() != null && produto.getPrecoDesconto() > 0
                        ? produto.getPrecoDesconto()
                        : produto.getPreco();

                String preco = precoFinal != null ? currencyFormat.format(precoFinal) : "Consulte preço";
                String sabor = produto.getSabor() != null ? " (" + produto.getSabor() + ")" : "";

                result.append(String.format("- %s%s: %s\n", produto.getNome(), sabor, preco));
            }

            return result.toString();

        } catch (Exception e) {
            return "Erro ao buscar produtos da categoria. Tente novamente.";
        }
    }

    public String getStoreInfo() {
        return """
        INFORMAÇÕES DA LOJA:
        - Frete grátis acima de R$ 99,00
        - Entrega: 3-7 dias úteis para todo o Brasil
        - Horário de atendimento: Segunda a sábado, 8h às 20h
        - Processamento de pedidos: até 24h úteis
        - Telefone: (11) 3333-4444
        - WhatsApp: (11) 99999-9999
        - Email: contato@supplementstore.com
        """;
    }

    public List<Produto> getAllActiveProducts() {
        Specification<Produto> spec = ProdutoSpecification.comFiltros(null, null, null, null, null, null);
        return produtoRepository.findAll(spec);
    }

    public Produto findProductByName(String name) {
        List<Produto> produtos = produtoRepository.findByTermo(name, null).getContent();
        return produtos.isEmpty() ? null : produtos.get(0);
    }
}

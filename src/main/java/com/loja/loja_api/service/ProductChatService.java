package com.loja.loja_api.service;

import com.loja.loja_api.model.Produto;
import com.loja.loja_api.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
public class ProductChatService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public String getProductsForChatbot() {
        try {
            // Busca produtos ativos (você pode precisar adicionar este método no repository)
            List<Produto> produtos = produtoRepository.findByAtivoTrue();

            if (produtos.isEmpty()) {
                return "PRODUTOS DISPONÍVEIS:\n- Nenhum produto ativo no momento. Entre em contato para mais informações.";
            }

            StringBuilder produtosInfo = new StringBuilder("PRODUTOS DISPONÍVEIS:\n");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

            for (Produto produto : produtos) {
                // Usa preço com desconto se disponível, senão preço normal
                Double precoFinal = produto.getPrecoDesconto() != null && produto.getPrecoDesconto() > 0
                        ? produto.getPrecoDesconto()
                        : produto.getPreco();

                String preco = precoFinal != null ?
                        currencyFormat.format(precoFinal) : "Consulte preço";

                // Monta descrição do produto
                String descricao = produto.getDescricaoCurta() != null ?
                        produto.getDescricaoCurta() :
                        (produto.getDescricao() != null ? produto.getDescricao() : "Suplemento de qualidade");

                // Adiciona informação de sabor se disponível
                String sabor = produto.getSabor() != null ? " (" + produto.getSabor() + ")" : "";

                produtosInfo.append(String.format(
                        "- %s%s: %s - %s\n",
                        produto.getNome(),
                        sabor,
                        preco,
                        descricao
                ));

                // Adiciona informação de estoque baixo se necessário
                if (produto.getEstoque() != null && produto.getEstoqueMinimo() != null &&
                        produto.getEstoque() <= produto.getEstoqueMinimo()) {
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
            List<Produto> produtos = produtoRepository.findByCategoriaIgnoreCaseAndAtivoTrue(categoria);

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
        return produtoRepository.findByAtivoTrue();
    }

    public Produto findProductByName(String name) {
        List<Produto> produtos = produtoRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(name);
        return produtos.isEmpty() ? null : produtos.get(0);
    }
}


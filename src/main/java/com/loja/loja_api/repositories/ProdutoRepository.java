package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    List<Produto> findByCategoriaIgnoreCase(String categoria);

    // Adicione estes métodos na interface ProdutoRepository:

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);

    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();
}

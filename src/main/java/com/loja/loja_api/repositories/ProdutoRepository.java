package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // --- Métodos com paginação (para o frontend) ---
    Page<Produto> findAll(Pageable pageable);
    Page<Produto> findByCategoriaIgnoreCase(String categoria, Pageable pageable);
    Page<Produto> findByAtivoTrue(Pageable pageable);
    Page<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria, Pageable pageable);
    Page<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    Page<Produto> findActiveProductsOnSale(Pageable pageable);

    // --- Métodos sem paginação (restaurados para o ProductChatService) ---
    List<Produto> findByCategoriaIgnoreCase(String categoria);
    List<Produto> findByAtivoTrue();
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();
}
package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    // --- Métodos com paginação (para o frontend) ---
    Page<Produto> findAll(Pageable pageable);

    // Método para filtrar por categoria, marca e preço
    @Query("SELECT p FROM Produto p WHERE " +
            "(:categoria IS NULL OR p.categoria = :categoria) AND " +
            "(p.marca IN :marcas) AND " +
            "(p.preco BETWEEN :minPreco AND :maxPreco) AND p.ativo = true")
    Page<Produto> findByFilters(@Param("categoria") String categoria,
                                @Param("marcas") List<String> marcas,
                                @Param("minPreco") Double minPreco,
                                @Param("maxPreco") Double maxPreco,
                                Pageable pageable);

    // Método para quando a lista de marcas está vazia
    @Query("SELECT p FROM Produto p WHERE " +
            "(:categoria IS NULL OR p.categoria = :categoria) AND " +
            "(p.preco BETWEEN :minPreco AND :maxPreco) AND p.ativo = true")
    Page<Produto> findByFiltersWithoutMarcas(@Param("categoria") String categoria,
                                             @Param("minPreco") Double minPreco,
                                             @Param("maxPreco") Double maxPreco,
                                             Pageable pageable);

    // Método para encontrar marcas ativas
    @Query("SELECT DISTINCT p.marca FROM Produto p WHERE p.ativo = true ORDER BY p.marca")
    List<String> findDistinctMarcas();


    // ✨ MÉTODOS RESTAURADOS PARA OUTROS COMPONENTES/SERVIÇOS

    @Query("SELECT p FROM Produto p WHERE p.ativo = true")
    List<Produto> findByAtivoTrue();

    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.ativo = true")
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(@Param("categoria") String categoria);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true")
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();
}
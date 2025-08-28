// src/main/java/com/loja/loja_api/repositories/ProdutoRepository.java

package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
import com.loja.loja_api.dto.CountedItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    Page<Produto> findAll(Pageable pageable);

    // Queries para listar categorias/marcas com a contagem de produtos
    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.marca, COUNT(p)) FROM Produto p WHERE p.ativo = true GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDto> findDistinctMarcasWithCount();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.categoria, COUNT(p)) FROM Produto p WHERE p.ativo = true GROUP BY p.categoria ORDER BY p.categoria")
    List<CountedItemDto> findDistinctCategoriasWithCount();

    // Queries que contam o total de categorias/marcas
    @Query("SELECT COUNT(DISTINCT p.marca) FROM Produto p WHERE p.ativo = true")
    Long countDistinctMarcas();

    @Query("SELECT COUNT(DISTINCT p.categoria) FROM Produto p WHERE p.ativo = true")
    Long countDistinctCategorias();

    // Consultas de filtro principal
    @Query("SELECT p FROM Produto p WHERE " +
            "(:categorias IS NULL OR p.categoria IN :categorias) AND " +
            "(:marcas IS NULL OR p.marca IN :marcas) AND " +
            "(p.preco BETWEEN :minPreco AND :maxPreco) AND p.ativo = true")
    Page<Produto> findByFilters(@Param("categorias") List<String> categorias,
                                @Param("marcas") List<String> marcas,
                                @Param("minPreco") Double minPreco,
                                @Param("maxPreco") Double maxPreco,
                                Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND (p.preco BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByPriceRange(@Param("minPreco") Double minPreco,
                                   @Param("maxPreco") Double maxPreco,
                                   Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.categoria IN :categorias AND (p.preco BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndPrice(@Param("categorias") List<String> categorias,
                                           @Param("minPreco") Double minPreco,
                                           @Param("maxPreco") Double maxPreco,
                                           Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.marca IN :marcas AND (p.preco BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByMarcasAndPrice(@Param("marcas") List<String> marcas,
                                       @Param("minPreco") Double minPreco,
                                       @Param("maxPreco") Double maxPreco,
                                       Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.categoria IN :categorias AND p.marca IN :marcas AND (p.preco BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndMarcasAndPrice(@Param("categorias") List<String> categorias,
                                                    @Param("marcas") List<String> marcas,
                                                    @Param("minPreco") Double minPreco,
                                                    @Param("maxPreco") Double maxPreco,
                                                    Pageable pageable);

    // Queries para buscar categorias/marcas com contagem com base em filtros cruzados
    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.marca, COUNT(p)) FROM Produto p WHERE p.ativo = true AND p.categoria IN :categorias GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDto> findDistinctMarcasByCategoriasWithCount(@Param("categorias") List<String> categorias);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.categoria, COUNT(p)) FROM Produto p WHERE p.ativo = true AND p.marca IN :marcas GROUP BY p.categoria ORDER BY p.categoria")
    List<CountedItemDto> findDistinctCategoriasByMarcasWithCount(@Param("marcas") List<String> marcas);

    // Métodos auxiliares originais
    @Query("SELECT p FROM Produto p WHERE p.ativo = true")
    List<Produto> findByAtivoTrue();

    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.ativo = true")
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(@Param("categoria") String categoria);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true")
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();
}
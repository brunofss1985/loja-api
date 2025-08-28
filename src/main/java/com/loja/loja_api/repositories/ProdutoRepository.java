package com.loja.loja_api.repositories;

import com.loja.loja_api.model.Produto;
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

    // --- Métodos com paginação (para o frontend) ---
    Page<Produto> findAll(Pageable pageable);

    // ✨ ATUALIZADO: Método para encontrar marcas ativas
    @Query("SELECT DISTINCT p.marca FROM Produto p WHERE p.ativo = true ORDER BY p.marca")
    List<String> findDistinctMarcas();

    // ✨ ATUALIZADO: Método para encontrar categorias ativas
    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.ativo = true ORDER BY p.categoria")
    List<String> findDistinctCategorias();

    // ✨ ATUALIZADO: Método para filtrar por categorias, marcas e preço
    @Query("SELECT p FROM Produto p WHERE " +
            "(:categorias IS NULL OR p.categoria IN :categorias) AND " +
            "(:marcas IS NULL OR p.marca IN :marcas) AND " +
            "(p.preco BETWEEN :minPreco AND :maxPreco) AND p.ativo = true")
    Page<Produto> findByFilters(@Param("categorias") List<String> categorias,
                                @Param("marcas") List<String> marcas,
                                @Param("minPreco") Double minPreco,
                                @Param("maxPreco") Double maxPreco,
                                Pageable pageable);

    // ✨ NOVO: Busca marcas com base nas categorias selecionadas
    @Query("SELECT DISTINCT p.marca FROM Produto p WHERE p.ativo = true AND p.categoria IN :categorias ORDER BY p.marca")
    List<String> findDistinctMarcasByCategorias(@Param("categorias") List<String> categorias);

    // ✨ NOVO: Busca categorias com base nas marcas selecionadas
    @Query("SELECT DISTINCT p.categoria FROM Produto p WHERE p.ativo = true AND p.marca IN :marcas ORDER BY p.categoria")
    List<String> findDistinctCategoriasByMarcas(@Param("marcas") List<String> marcas);

    // Métodos restaurados
    @Query("SELECT p FROM Produto p WHERE p.ativo = true")
    List<Produto> findByAtivoTrue();

    @Query("SELECT p FROM Produto p WHERE p.categoria = :categoria AND p.ativo = true")
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(@Param("categoria") String categoria);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true")
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();
}
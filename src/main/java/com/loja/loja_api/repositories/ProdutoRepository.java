package com.loja.loja_api.repositories;

import com.loja.loja_api.dto.CountedItemDto;
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

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.destaque = true")
    Page<Produto> findByDestaqueAndAtivoTrue(Pageable pageable);

    Page<Produto> findAll(Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND " +
            "(LOWER(p.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.marca) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(p.descricaoCurta) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "EXISTS (SELECT c FROM p.categorias c WHERE LOWER(c) LIKE LOWER(CONCAT('%', :termo, '%'))) OR " +
            "EXISTS (SELECT o FROM p.objetivos o WHERE LOWER(o) LIKE LOWER(CONCAT('%', :termo, '%'))))")
    Page<Produto> findByTermo(@Param("termo") String termo, Pageable pageable);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.marca, COUNT(p)) FROM Produto p WHERE p.ativo = true GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDto> findDistinctMarcasWithCount();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(c, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true GROUP BY c ORDER BY COUNT(p) DESC")
    List<CountedItemDto> findDistinctCategoriasWithCount();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(o, COUNT(p)) FROM Produto p JOIN p.objetivos o WHERE p.ativo = true GROUP BY o ORDER BY COUNT(p) DESC")
    List<CountedItemDto> findDistinctObjetivosWithCount();

    @Query("SELECT COUNT(DISTINCT p.marca) FROM Produto p WHERE p.ativo = true")
    Long countDistinctMarcas();

    @Query("SELECT COUNT(DISTINCT c) FROM Produto p JOIN p.categorias c WHERE p.ativo = true")
    Long countDistinctCategorias();

    @Query("SELECT COUNT(DISTINCT o) FROM Produto p JOIN p.objetivos o WHERE p.ativo = true")
    Long countDistinctObjetivos();

    // Consultas originais com preço
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByPriceRange(@Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.objetivos o WHERE p.ativo = true AND o IN :objetivos AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByObjetivosAndPrice(@Param("objetivos") List<String> objetivos, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndPrice(@Param("categorias") List<String> categorias, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.marca IN :marcas AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByMarcasAndPrice(@Param("marcas") List<String> marcas, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c JOIN p.objetivos o WHERE p.ativo = true AND c IN :categorias AND o IN :objetivos AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndObjetivosAndPrice(@Param("categorias") List<String> categorias, @Param("objetivos") List<String> objetivos, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.objetivos o WHERE p.ativo = true AND p.marca IN :marcas AND o IN :objetivos AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByMarcasAndObjetivosAndPrice(@Param("marcas") List<String> marcas, @Param("objetivos") List<String> objetivos, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias AND p.marca IN :marcas AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndMarcasAndPrice(@Param("categorias") List<String> categorias, @Param("marcas") List<String> marcas, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c JOIN p.objetivos o WHERE p.ativo = true AND c IN :categorias AND p.marca IN :marcas AND o IN :objetivos AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndMarcasAndObjetivosAndPrice(@Param("categorias") List<String> categorias, @Param("marcas") List<String> marcas, @Param("objetivos") List<String> objetivos, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    // ✅ Novas consultas para o campo 'destaque'
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByDestaqueAndPrice(@Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndDestaqueAndPrice(@Param("categorias") List<String> categorias, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.marca IN :marcas AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByMarcasAndDestaqueAndPrice(@Param("marcas") List<String> marcas, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.objetivos o WHERE p.ativo = true AND o IN :objetivos AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByObjetivosAndDestaqueAndPrice(@Param("objetivos") List<String> objetivos, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c JOIN p.objetivos o WHERE p.ativo = true AND c IN :categorias AND p.marca IN :marcas AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndMarcasAndDestaqueAndPrice(@Param("categorias") List<String> categorias, @Param("marcas") List<String> marcas, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c JOIN p.objetivos o WHERE p.ativo = true AND c IN :categorias AND o IN :objetivos AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndObjetivosAndDestaqueAndPrice(@Param("categorias") List<String> categorias, @Param("objetivos") List<String> objetivos, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.objetivos o WHERE p.ativo = true AND p.marca IN :marcas AND o IN :objetivos AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByMarcasAndObjetivosAndDestaqueAndPrice(@Param("marcas") List<String> marcas, @Param("objetivos") List<String> objetivos, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Produto p JOIN p.categorias c JOIN p.objetivos o WHERE p.ativo = true AND c IN :categorias AND p.marca IN :marcas AND o IN :objetivos AND p.destaque = :destaque AND (CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findByCategoriasAndMarcasAndObjetivosAndDestaqueAndPrice(@Param("categorias") List<String> categorias, @Param("marcas") List<String> marcas, @Param("objetivos") List<String> objetivos, @Param("destaque") Boolean destaque, @Param("minPreco") Double minPreco, @Param("maxPreco") Double maxPreco, Pageable pageable);


    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(p.marca, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDto> findDistinctMarcasByCategoriasWithCount(@Param("categorias") List<String> categorias);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(c, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND p.marca IN :marcas GROUP BY c ORDER BY COUNT(p) DESC")
    List<CountedItemDto> findDistinctCategoriasByMarcasWithCount(@Param("marcas") List<String> marcas);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDto(o, COUNT(p)) FROM Produto p JOIN p.objetivos o JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias GROUP BY o ORDER BY o")
    List<CountedItemDto> findDistinctObjetivosByCategoriasWithCount(@Param("categorias") List<String> categorias);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true")
    List<Produto> findByAtivoTrue();

    @Query("SELECT p FROM Produto p JOIN p.categorias c WHERE c = :categoria AND p.ativo = true")
    List<Produto> findByCategoriaIgnoreCaseAndAtivoTrue(@Param("categoria") String categoria);

    @Query("SELECT p FROM Produto p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%')) AND p.ativo = true")
    List<Produto> findByNomeContainingIgnoreCaseAndAtivoTrue(@Param("nome") String nome);

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.precoDesconto IS NOT NULL AND p.precoDesconto > 0")
    List<Produto> findActiveProductsOnSale();

    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.marca = :marca AND " +
            "(CASE WHEN p.precoDesconto > 0 THEN p.precoDesconto ELSE p.preco END BETWEEN :minPreco AND :maxPreco)")
    Page<Produto> findBySingleMarcaAndPrice(@Param("marca") String marca,
                                            @Param("minPreco") Double minPreco,
                                            @Param("maxPreco") Double maxPreco,
                                            Pageable pageable);
}
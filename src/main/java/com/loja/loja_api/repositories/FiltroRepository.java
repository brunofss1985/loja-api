package com.loja.loja_api.repositories;

import com.loja.loja_api.dto.CountedItemDTO;
import com.loja.loja_api.models.Produto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FiltroRepository extends org.springframework.data.repository.Repository<Produto, Long> {

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(p.marca, COUNT(p)) FROM Produto p WHERE p.ativo = true GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDTO> findDistinctMarcasWithCount();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(c, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true GROUP BY c ORDER BY COUNT(p) DESC")
    List<CountedItemDTO> findDistinctCategoriasWithCount();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(o, COUNT(p)) FROM Produto p JOIN p.objetivos o WHERE p.ativo = true GROUP BY o ORDER BY COUNT(p) DESC")
    List<CountedItemDTO> findDistinctObjetivosWithCount();

    @Query("SELECT COUNT(DISTINCT p.marca) FROM Produto p WHERE p.ativo = true")
    Long countDistinctMarcas();

    @Query("SELECT COUNT(DISTINCT c) FROM Produto p JOIN p.categorias c WHERE p.ativo = true")
    Long countDistinctCategorias();

    @Query("SELECT COUNT(DISTINCT o) FROM Produto p JOIN p.objetivos o WHERE p.ativo = true")
    Long countDistinctObjetivos();

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(p.marca, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias GROUP BY p.marca ORDER BY p.marca")
    List<CountedItemDTO> findDistinctMarcasByCategoriasWithCount(@Param("categorias") List<String> categorias);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(c, COUNT(p)) FROM Produto p JOIN p.categorias c WHERE p.ativo = true AND p.marca IN :marcas GROUP BY c ORDER BY COUNT(p) DESC")
    List<CountedItemDTO> findDistinctCategoriasByMarcasWithCount(@Param("marcas") List<String> marcas);

    @Query("SELECT new com.loja.loja_api.dto.CountedItemDTO(o, COUNT(p)) FROM Produto p JOIN p.objetivos o JOIN p.categorias c WHERE p.ativo = true AND c IN :categorias GROUP BY o")
    List<CountedItemDTO> findDistinctObjetivosByCategoriasWithCount(@Param("categorias") List<String> categorias);
}
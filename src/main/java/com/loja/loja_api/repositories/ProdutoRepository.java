package com.loja.loja_api.repositories;

import com.loja.loja_api.models.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {


    @Query("SELECT p FROM Produto p WHERE p.destaque = true AND p.ativo = true")
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

}
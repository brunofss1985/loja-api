package com.loja.loja_api.repositories;

import com.loja.loja_api.models.ProdutoReal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProdutoRealRepository extends JpaRepository<ProdutoReal, Long> {
    List<ProdutoReal> findByLoteId(Long loteId);

    @Query("SELECT COALESCE(SUM(pr.quantidade), 0) FROM ProdutoReal pr WHERE pr.lote.id = :loteId")
    Integer sumQuantidadeByLoteId(Long loteId);

    // ✅ Soma total por produtoId
    @Query("SELECT COALESCE(SUM(pr.quantidade), 0) FROM ProdutoReal pr WHERE pr.produto.id = :produtoId")
    Integer sumQuantidadeByProdutoId(Long produtoId);

    // ✅ NOVO: Deleta todos os registros vinculados ao lote
    @Transactional
    void deleteByLoteId(Long loteId);
}

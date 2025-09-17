package com.loja.loja_api.repositories;

import com.loja.loja_api.models.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoteRepository extends JpaRepository<Lote, Long> {

    // Busca todos os lotes já com produto (só campos básicos)
    @Query("SELECT l FROM Lote l JOIN FETCH l.produto p")
    List<Lote> findAllWithProduto();

    // Busca lote por id já com produto
    @Query("SELECT l FROM Lote l JOIN FETCH l.produto p WHERE l.id = :id")
    Optional<Lote> findByIdWithProduto(Long id);
}

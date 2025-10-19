package com.loja.loja_api.repositories;

import com.loja.loja_api.models.ProdutoVendido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoVendidoRepository extends JpaRepository<ProdutoVendido, Long> {
}

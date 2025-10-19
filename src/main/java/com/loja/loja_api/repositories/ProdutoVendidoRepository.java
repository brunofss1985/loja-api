package com.loja.loja_api.repositories;

import com.loja.loja_api.models.ProdutoVendido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProdutoVendidoRepository extends JpaRepository<ProdutoVendido, Long> {

	// Verifica se já existe registro de venda para um produto específico dentro de um pedido
	long countByOrder_IdAndProduto_Id(Long orderId, Long produtoId);
}

package com.loja.loja_api.services;

import com.loja.loja_api.dto.ProdutoVendidoDTO;
import com.loja.loja_api.enums.OrderStatus;
import com.loja.loja_api.models.*;
import com.loja.loja_api.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final OrderRepository orderRepository;
    private final ProdutoRealRepository produtoRealRepository;
    private final ProdutoVendidoRepository produtoVendidoRepository;
    private final OrderStatusHistoryRepository statusHistoryRepository;

    @Transactional
    public ProdutoVendidoDTO finalizarVenda(Long orderId, String codigoBarras, String loteCodigo) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (order.getStatus() != OrderStatus.PAID && order.getStatus() != OrderStatus.DESPACHADO) {
            throw new IllegalStateException("Pedido precisa estar pago ou despachado para finalizar venda");
        }

        // localizar produto real pelo código de barras e lote
        ProdutoReal pr = produtoRealRepository.findFirstByCodigoBarrasAndLote_Codigo(codigoBarras, loteCodigo);
        if (pr == null) {
            throw new EntityNotFoundException("Código de barras não encontrado neste lote");
        }

        Produto produto = pr.getProduto();

        // Evitar finalizar mais de uma vez o mesmo produto no mesmo pedido
        if (produto != null) {
            long jaFinalizado = produtoVendidoRepository.countByOrder_IdAndProduto_Id(orderId, produto.getId());
            if (jaFinalizado > 0) {
                throw new IllegalStateException("Este produto já foi finalizado para este pedido.");
            }
        }

        // registrar produto vendido
        ProdutoVendido vendido = ProdutoVendido.builder()
                .produto(produto)
                .order(order)
                .produtoNome(produto != null ? produto.getNome() : null)
                .customerNome(order.getCustomer() != null ? order.getCustomer().getFullName() : null)
                .customerEmail(order.getCustomer() != null ? order.getCustomer().getEmail() : null)
                .codigoBarras(pr.getCodigoBarras())
                .loteCodigo(pr.getLote() != null ? pr.getLote().getCodigo() : loteCodigo)
                .dataVenda(Instant.now())
                .valorVenda(guessValorVenda(order))
                .build();
        produtoVendidoRepository.save(vendido);

        // remover do estoque real (e por consequência do lote e estoque total calculado)
        produtoRealRepository.delete(pr);

    // Atualiza status do pedido para DESPACHADO e registra histórico
    order.setStatus(OrderStatus.DESPACHADO);
    OrderStatusHistory hist = OrderStatusHistory.builder()
        .order(order)
        .status(OrderStatus.DESPACHADO.name())
        .changedAt(Instant.now())
        .build();
    statusHistoryRepository.save(hist);

        return ProdutoVendidoDTO.fromEntity(vendido);
    }

    private BigDecimal guessValorVenda(Order order) {
        // melhor esforço: usa total do pedido dividido por itens totais
        int totalQtd = order.getItems() != null
                ? order.getItems().stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum()
                : 1;
        if (totalQtd <= 0) totalQtd = 1;
        return order.getTotal() != null ? order.getTotal().divide(BigDecimal.valueOf(totalQtd)) : BigDecimal.ZERO;
    }
}

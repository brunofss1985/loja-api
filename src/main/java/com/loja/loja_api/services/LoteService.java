package com.loja.loja_api.services;

import com.loja.loja_api.dto.LoteDTO;
import com.loja.loja_api.models.Lote;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.LoteRepository;
import com.loja.loja_api.repositories.ProdutoRealRepository;
import com.loja.loja_api.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository repository;
    private final ProdutoRepository produtoRepository;
    private final ProdutoRealRepository produtoRealRepository;

    @Transactional(readOnly = true)
    public List<LoteDTO> listarTodos() {
        return repository.findAllWithProduto().stream()
                .map(lote -> {
                    lote.setQuantidade(produtoRealRepository.sumQuantidadeByLoteId(lote.getId()));
                    return LoteDTO.fromEntity(lote);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoteDTO buscarPorId(Long id) {
        Lote lote = repository.findByIdWithProduto(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));

        lote.setQuantidade(produtoRealRepository.sumQuantidadeByLoteId(lote.getId()));
        return LoteDTO.fromEntity(lote);
    }

    @Transactional
    public LoteDTO criar(LoteDTO dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Integer quantidade = dto.getQuantidadeTotal() != null ? dto.getQuantidadeTotal() : 0;
        Double custoUnit = dto.getCustoPorUnidade() != null ? dto.getCustoPorUnidade() : 0.0;
        Double valorVendaSugerido = dto.getValorVendaSugerido() != null ? dto.getValorVendaSugerido() : 0.0;

        // ✅ Cálculos automáticos
        Double custoTotal = custoUnit * quantidade;
        Double lucroUnit = valorVendaSugerido - custoUnit;
        Double lucroTotal = lucroUnit * quantidade;

        Lote lote = Lote.builder()
                .codigo(dto.getCodigo())
                .dataValidade(dto.getDataValidade())
                .fornecedor(dto.getFornecedor())
                .custoPorUnidade(custoUnit)
                .localArmazenamento(dto.getLocalArmazenamento())
                .statusLote(dto.getStatusLote())
                .dataRecebimento(dto.getDataRecebimento())
                .valorVendaSugerido(valorVendaSugerido)
                .notaFiscalEntrada(dto.getNotaFiscalEntrada())
                .contatoVendedor(dto.getContatoVendedor())
                .produto(produto)

                // ✅ Novos campos calculados
                .custoTotalLote(custoTotal)
                .lucroEstimadoPorUnidade(lucroUnit)
                .lucroTotalEstimado(lucroTotal)
                .codigoBarras(dto.getCodigoBarras())
                .cnpjFornecedor(dto.getCnpjFornecedor())
                .dataCadastro(LocalDate.now())
                .dataAtualizacao(LocalDate.now())
                .build();

        return LoteDTO.fromEntity(repository.save(lote));
    }

    @Transactional
    public LoteDTO atualizar(Long id, LoteDTO dto) {
        Lote lote = repository.findByIdWithProduto(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));

        Integer quantidade = dto.getQuantidadeTotal() != null ? dto.getQuantidadeTotal() : 0;
        Double custoUnit = dto.getCustoPorUnidade() != null ? dto.getCustoPorUnidade() : 0.0;
        Double valorVendaSugerido = dto.getValorVendaSugerido() != null ? dto.getValorVendaSugerido() : 0.0;

        // ✅ Cálculos automáticos
        Double custoTotal = custoUnit * quantidade;
        Double lucroUnit = valorVendaSugerido - custoUnit;
        Double lucroTotal = lucroUnit * quantidade;

        lote.setCodigo(dto.getCodigo());
        lote.setDataValidade(dto.getDataValidade());
        lote.setFornecedor(dto.getFornecedor());
        lote.setCustoPorUnidade(custoUnit);
        lote.setLocalArmazenamento(dto.getLocalArmazenamento());
        lote.setStatusLote(dto.getStatusLote());
        lote.setDataRecebimento(dto.getDataRecebimento());
        lote.setValorVendaSugerido(valorVendaSugerido);
        lote.setNotaFiscalEntrada(dto.getNotaFiscalEntrada());
        lote.setContatoVendedor(dto.getContatoVendedor());

        // ✅ Campos calculados novamente
        lote.setCustoTotalLote(custoTotal);
        lote.setLucroEstimadoPorUnidade(lucroUnit);
        lote.setLucroTotalEstimado(lucroTotal);
        lote.setCodigoBarras(dto.getCodigoBarras());
        lote.setCnpjFornecedor(dto.getCnpjFornecedor());
        lote.setDataAtualizacao(LocalDate.now());

        return LoteDTO.fromEntity(repository.save(lote));
    }

    @Transactional
    public void remover(Long id) {
        produtoRealRepository.deleteByLoteId(id);
        repository.deleteById(id);
    }
}

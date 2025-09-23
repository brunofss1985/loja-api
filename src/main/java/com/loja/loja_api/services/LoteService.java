package com.loja.loja_api.services;

import com.loja.loja_api.dto.LoteDTO;
import com.loja.loja_api.models.Lote;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.repositories.LoteRepository;
import com.loja.loja_api.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoteService {

    private final LoteRepository repository;
    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<LoteDTO> listarTodos() {
        return repository.findAll().stream()
                .map(LoteDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public LoteDTO buscarPorId(Long id) {
        Lote lote = repository.findByIdWithProduto(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));
        return LoteDTO.fromEntity(lote);
    }

    @Transactional
    public LoteDTO criar(LoteDTO dto) {
        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        Lote lote = Lote.builder()
                .codigo(dto.getCodigo())
                .dataValidade(dto.getDataValidade())
                .fornecedor(dto.getFornecedor())
                .custoPorUnidade(dto.getCustoPorUnidade())
                .localArmazenamento(dto.getLocalArmazenamento())
                .statusLote(dto.getStatusLote())
                .dataRecebimento(dto.getDataRecebimento())
                .valorVendaSugerido(dto.getValorVendaSugerido())
                .notaFiscalEntrada(dto.getNotaFiscalEntrada())
                .contatoVendedor(dto.getContatoVendedor())
                .produto(produto)
                .build();

        return LoteDTO.fromEntity(repository.save(lote));
    }

    @Transactional
    public LoteDTO atualizar(Long id, LoteDTO dto) {
        Lote lote = repository.findByIdWithProduto(id)
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));

        lote.setCodigo(dto.getCodigo());
        lote.setDataValidade(dto.getDataValidade());
        lote.setFornecedor(dto.getFornecedor());
        lote.setCustoPorUnidade(dto.getCustoPorUnidade());
        lote.setLocalArmazenamento(dto.getLocalArmazenamento());
        lote.setStatusLote(dto.getStatusLote());
        lote.setDataRecebimento(dto.getDataRecebimento());
        lote.setValorVendaSugerido(dto.getValorVendaSugerido());
        lote.setNotaFiscalEntrada(dto.getNotaFiscalEntrada());
        lote.setContatoVendedor(dto.getContatoVendedor());

        return LoteDTO.fromEntity(repository.save(lote));
    }


    @Transactional
    public void remover(Long id) {
        repository.deleteById(id);
    }
}

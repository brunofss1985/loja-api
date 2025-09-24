package com.loja.loja_api.services;

import com.loja.loja_api.dto.ProdutoRealDTO;
import com.loja.loja_api.models.Lote;
import com.loja.loja_api.models.Produto;
import com.loja.loja_api.models.ProdutoReal;
import com.loja.loja_api.repositories.LoteRepository;
import com.loja.loja_api.repositories.ProdutoRealRepository;
import com.loja.loja_api.repositories.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoRealService {

    private final ProdutoRealRepository repository;
    private final ProdutoRepository produtoRepository;
    private final LoteRepository loteRepository;

    @Transactional
    public ProdutoRealDTO salvar(ProdutoRealDTO dto) {
        Lote lote = loteRepository.findById(dto.getLoteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        ProdutoReal pr = ProdutoReal.builder()
                .quantidade(dto.getQuantidade())
                .codigoBarras(dto.getCodigoBarras())
                .localizacaoFisica(dto.getLocalizacaoFisica())
                .dataValidade(dto.getDataValidade())
                .lote(lote)
                .produto(produto)
                .build();

        return ProdutoRealDTO.fromEntity(repository.save(pr));
    }

    @Transactional(readOnly = true)
    public List<ProdutoRealDTO> listarPorLote(Long loteId) {
        return repository.findByLoteId(loteId).stream()
                .map(ProdutoRealDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public int obterEstoqueTotalPorProdutoId(Long produtoId) {
        return repository.sumQuantidadeByProdutoId(produtoId);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }
}

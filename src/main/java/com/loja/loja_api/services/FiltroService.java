package com.loja.loja_api.services;

import com.loja.loja_api.dto.CountedItemDTO;
import com.loja.loja_api.repositories.FiltroRepository;
import com.loja.loja_api.util.ListUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FiltroService {

    @Autowired
    private FiltroRepository filtroRepository;

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarMarcas() {
        return filtroRepository.findDistinctMarcasWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarCategorias() {
        return filtroRepository.findDistinctCategoriasWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarMarcasPorCategorias(List<String> categorias) {
        List<String> norm = ListUtils.normalizeList(categorias);
        if (norm == null || norm.isEmpty()) {
            return listarMarcas();
        }
        return filtroRepository.findDistinctMarcasByCategoriasWithCount(norm);
    }

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarCategoriasPorMarcas(List<String> marcas) {
        List<String> norm = ListUtils.normalizeList(marcas);
        if (norm == null || norm.isEmpty()) {
            return listarCategorias();
        }
        return filtroRepository.findDistinctCategoriasByMarcasWithCount(norm);
    }

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarObjetivos() {
        return filtroRepository.findDistinctObjetivosWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDTO> listarObjetivosPorCategorias(List<String> categorias) {
        List<String> norm = ListUtils.normalizeList(categorias);
        if (norm == null || norm.isEmpty()) {
            return listarObjetivos();
        }
        return filtroRepository.findDistinctObjetivosByCategoriasWithCount(norm);
    }

    @Transactional(readOnly = true)
    public Long contarMarcas() {
        return filtroRepository.countDistinctMarcas();
    }

    @Transactional(readOnly = true)
    public Long contarCategorias() {
        return filtroRepository.countDistinctCategorias();
    }

    @Transactional(readOnly = true)
    public Long contarObjetivos() {
        return filtroRepository.countDistinctObjetivos();
    }
}
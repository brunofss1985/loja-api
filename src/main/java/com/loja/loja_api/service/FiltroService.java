package com.loja.loja_api.service;

import com.loja.loja_api.dto.CountedItemDto;
import com.loja.loja_api.repositories.FiltroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class FiltroService {

    @Autowired
    private FiltroRepository filtroRepository;

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarMarcas() {
        return filtroRepository.findDistinctMarcasWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarCategorias() {
        return filtroRepository.findDistinctCategoriasWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarMarcasPorCategorias(List<String> categorias) {
        List<String> norm = normalizeList(categorias);
        if (norm == null || norm.isEmpty()) {
            return listarMarcas();
        }
        return filtroRepository.findDistinctMarcasByCategoriasWithCount(norm);
    }

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarCategoriasPorMarcas(List<String> marcas) {
        List<String> norm = normalizeList(marcas);
        if (norm == null || norm.isEmpty()) {
            return listarCategorias();
        }
        return filtroRepository.findDistinctCategoriasByMarcasWithCount(norm);
    }

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarObjetivos() {
        return filtroRepository.findDistinctObjetivosWithCount();
    }

    @Transactional(readOnly = true)
    public List<CountedItemDto> listarObjetivosPorCategorias(List<String> categorias) {
        List<String> norm = normalizeList(categorias);
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

    private List<String> normalizeList(List<String> raw) {
        if (raw == null || raw.isEmpty()) return null;
        List<String> out = new ArrayList<>();
        for (String s : raw) {
            if (s == null) continue;
            for (String p : s.split(",")) {
                String t = p.trim();
                if (!t.isEmpty()) out.add(t);
            }
        }
        return out.isEmpty() ? null : out;
    }
}
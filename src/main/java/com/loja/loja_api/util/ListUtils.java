package com.loja.loja_api.util;

import java.util.ArrayList;
import java.util.List;

public final class ListUtils {

    // ✅ O construtor é privado para impedir a criação de instâncias.
    private ListUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Normaliza uma lista de Strings, removendo valores nulos ou vazios e dividindo
     * strings que contêm vírgulas.
     * @param raw A lista de Strings a ser normalizada.
     * @return Uma nova lista de Strings normalizada, ou null se a entrada for nula ou vazia.
     */
    public static List<String> normalizeList(List<String> raw) {
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
package com.loja.loja_api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImagemService {

    /**
     * Processa um único arquivo de imagem, convertendo-o para um array de bytes.
     * @param imagem O arquivo MultipartFile da imagem.
     * @return O array de bytes da imagem, ou null se o arquivo estiver vazio.
     */
    public byte[] processarImagem(MultipartFile imagem) {
        if (imagem != null && !imagem.isEmpty()) {
            try {
                return imagem.getBytes();
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar a imagem: " + e.getMessage());
            }
        }
        return null;
    }

    /**
     * Processa uma lista de arquivos para a galeria, convertendo-os para arrays de bytes.
     * @param galeriaArquivos A lista de arquivos MultipartFile da galeria.
     * @return Uma lista de arrays de bytes, ou null se a lista de arquivos estiver vazia.
     */
    public List<byte[]> processarGaleria(List<MultipartFile> galeriaArquivos) {
        if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
            List<byte[]> galeriaBytes = new ArrayList<>();
            for (MultipartFile file : galeriaArquivos) {
                if (!file.isEmpty()) {
                    try {
                        galeriaBytes.add(file.getBytes());
                    } catch (IOException e) {
                        throw new RuntimeException("Erro ao processar a galeria de imagens: " + e.getMessage());
                    }
                }
            }
            return galeriaBytes;
        }
        return null;
    }

    /**
     * Obtém o tipo MIME de um arquivo de imagem.
     * @param imagem O arquivo MultipartFile da imagem.
     * @return O tipo MIME da imagem, ou null se o arquivo estiver vazio.
     */
    public String getImagemMimeType(MultipartFile imagem) {
        return (imagem != null && !imagem.isEmpty()) ? imagem.getContentType() : null;
    }

    /**
     * Obtém os tipos MIME para uma lista de arquivos da galeria.
     * @param galeriaArquivos A lista de arquivos MultipartFile da galeria.
     * @return Uma lista de tipos MIME, ou null se a lista estiver vazia.
     */
    public List<String> getGaleriaMimeTypes(List<MultipartFile> galeriaArquivos) {
        if (galeriaArquivos != null && !galeriaArquivos.isEmpty()) {
            List<String> mimeTypes = new ArrayList<>();
            for (MultipartFile file : galeriaArquivos) {
                if (!file.isEmpty()) {
                    mimeTypes.add(file.getContentType());
                }
            }
            return mimeTypes;
        }
        return null;
    }
}
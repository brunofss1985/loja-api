package com.loja.loja_api.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/imagens")
public class ImagemController {

    @Value("${caminho.uploads:uploads}")
    private String uploadDir;

    @GetMapping("/{nome}")
    public ResponseEntity<Resource> servirImagem(@PathVariable String nome) {
        try {
            Path caminho = Paths.get(uploadDir).resolve(nome).normalize();
            Resource recurso = new UrlResource(caminho.toUri());

            if (recurso.exists()) {
                MediaType tipo = MediaTypeFactory.getMediaType(recurso)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                        .contentType(tipo)
                        .body(recurso);
            }

            return ResponseEntity.notFound().build();

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

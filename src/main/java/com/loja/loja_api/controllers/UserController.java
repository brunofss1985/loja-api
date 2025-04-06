package com.loja.loja_api.controllers;

import com.loja.loja_api.domain.user.User;
import com.loja.loja_api.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public ResponseEntity<?> getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(new ResponseDTO(user.getName(), null));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<String> adminOnly() {
        return ResponseEntity.ok("Acesso liberado para ADMIN");
    }
}

package com.loja.loja_api.controllers;

import com.loja.loja_api.dto.PasswordChangeDTO;
import com.loja.loja_api.dto.SetPasswordDTO; // Importe o novo DTO
import com.loja.loja_api.models.User;
import com.loja.loja_api.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<User> getUser() {
        return userService.getCurrentUser()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<User> updateOwnProfile(@RequestBody User updatedUser) {
        return userService.updateOwnProfile(updatedUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(401).build());
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordChangeDTO passwordChangeDto) {
        var result = userService.changePassword(
                passwordChangeDto.getCurrentPassword(),
                passwordChangeDto.getNewPassword()
        );
        return result.isSuccess()
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(result.getMessage());
    }

    // 🎯 Novo endpoint para criar a senha
    @PutMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody SetPasswordDTO setPasswordDTO) {
        var result = userService.setPassword(setPasswordDTO.getNewPassword());
        return result.isSuccess()
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(result.getMessage());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<User> getByEmail(@PathVariable String email) {
        return userService.getByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable String id, @RequestBody User updatedUser) {
        return userService.updateUser(id, updatedUser)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
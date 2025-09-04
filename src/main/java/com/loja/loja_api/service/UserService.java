package com.loja.loja_api.service;

import com.loja.loja_api.model.ChangePasswordResult;
import com.loja.loja_api.model.User;
import com.loja.loja_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ Otimizado: Lógica para buscar o usuário logado de forma mais concisa.
    public Optional<User> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }
        var userDetails = (User) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId());
    }

    public ChangePasswordResult changePassword(String currentPassword, String newPassword) {
        var currentUserOpt = getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return ChangePasswordResult.failure("Usuário não encontrado.");
        }
        var currentUser = currentUserOpt.get();

        if (!passwordEncoder.matches(currentPassword, currentUser.getPassword())) {
            return ChangePasswordResult.failure("Senha antiga incorreta.");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        return ChangePasswordResult.success();
    }

    // ✅ Mantido: Métodos simples de busca no repositório.
    public Optional<User> getById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        // 🔐 Adicionando lógica de segurança para o createUser
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // ✅ Refatorado: Lógica de atualização centralizada aqui.
    public Optional<User> updateUser(String id, User updatedUser) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            // 🔐 Apenas o Service sabe como atualizar o userType de forma segura
            if (updatedUser.getUserType() != null) {
                existing.setUserType(updatedUser.getUserType());
            }
            existing.setPhone(updatedUser.getPhone());
            existing.setAddress(updatedUser.getAddress());
            existing.setPoints(updatedUser.getPoints());
            existing.setCredits(updatedUser.getCredits());
            return userRepository.save(existing);
        });
    }

    // ✅ Otimizado: Lógica de atualização de perfil simplificada.
    public Optional<User> updateOwnProfile(User updatedUser) {
        var currentUserOpt = getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return Optional.empty();
        }

        var currentUser = currentUserOpt.get();
        currentUser.setName(updatedUser.getName());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setAddress(updatedUser.getAddress());
        currentUser.setCredits(updatedUser.getCredits());
        currentUser.setPoints(updatedUser.getPoints());

        return Optional.of(userRepository.save(currentUser));
    }

    public boolean deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }
}
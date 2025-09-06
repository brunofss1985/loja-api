package com.loja.loja_api.services;

import com.loja.loja_api.models.ChangePasswordResult;
import com.loja.loja_api.models.User;
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

    public Optional<User> getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal().equals("anonymousUser")) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User) {
            return Optional.of((User) principal);
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String userId = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return userRepository.findById(userId);
        }

        return Optional.empty();
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

    // 🎯 Novo método para definir a senha do usuário
    public ChangePasswordResult setPassword(String newPassword) {
        var currentUserOpt = getCurrentUser();
        if (currentUserOpt.isEmpty()) {
            return ChangePasswordResult.failure("Usuário não encontrado.");
        }
        var currentUser = currentUserOpt.get();

        if (currentUser.getPassword() != null && !currentUser.getPassword().isEmpty()) {
            return ChangePasswordResult.failure("A senha já foi definida. Por favor, use a opção de 'trocar senha'.");
        }

        if (newPassword == null || newPassword.length() < 6) {
            return ChangePasswordResult.failure("A nova senha deve ter pelo menos 6 caracteres.");
        }

        currentUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(currentUser);

        return ChangePasswordResult.success();
    }

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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> updateUser(String id, User updatedUser) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
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
package com.loja.loja_api.service;

import com.loja.loja_api.model.User;
import com.loja.loja_api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userDetails = (User) authentication.getPrincipal();
        return userRepository.findById(userDetails.getId()).orElse(null);
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
        return userRepository.save(user);
    }

    public Optional<User> updateUser(String id, User updatedUser) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(updatedUser.getName());
            existing.setEmail(updatedUser.getEmail());
            existing.setUserType(updatedUser.getUserType());
            existing.setPhone(updatedUser.getPhone());
            existing.setAddress(updatedUser.getAddress());
            existing.setPoints(updatedUser.getPoints());
            existing.setCredits(updatedUser.getCredits());
            return userRepository.save(existing);
        });
    }

    public boolean deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public Optional<User> updateOwnProfile(User updatedUser) {
        var currentUser = getCurrentUser();
        if (currentUser == null) return Optional.empty();

        currentUser.setName(updatedUser.getName());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setAddress(updatedUser.getAddress());
        currentUser.setCredits(updatedUser.getCredits());
        currentUser.setPoints(updatedUser.getPoints());

        return Optional.of(userRepository.save(currentUser));
    }
}

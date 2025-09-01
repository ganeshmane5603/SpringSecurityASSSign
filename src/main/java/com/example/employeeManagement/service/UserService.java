package com.example.employeeManagement.service;


import com.example.employeeManagement.dto.RegisterRequest;
import com.example.employeeManagement.entity.User;
import com.example.employeeManagement.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public User registerNewUser(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        // Accept role as provided; ensure it has "ROLE_" prefix in client or normalize here:
        String role = req.getRole();
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        user.setRole(role);
        return userRepository.save(user);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }
}

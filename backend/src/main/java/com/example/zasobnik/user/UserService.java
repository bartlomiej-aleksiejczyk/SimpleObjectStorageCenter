package com.example.zasobnik.user;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import lombok.RequiredArgsConstructor;

import com.example.zasobnik.user.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createUser(String username, String rawPassword) {
        if (userRepository.findByUsername(username) == null) {
            Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            String encodedPassword = "{argon2@SpringSecurity_v5_8}" + encoder.encode(rawPassword);

            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword);
            user.setRoles("ROLE_USER");

            userRepository.save(user);
        }
    }
}

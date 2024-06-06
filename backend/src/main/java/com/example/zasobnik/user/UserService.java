package com.example.zasobnik.user;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void createAdmin(String username, String rawPassword) {
        if (userRepository.findByUsername(username) == null) {
            Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
            String encodedPassword = "{argon2@SpringSecurity_v5_8}" + encoder.encode(rawPassword);

            User user = new User();
            user.setUsername(username);
            user.setPassword(encodedPassword);
            user.setRole(UserRole.ROLE_ADMINISTRATOR);

            userRepository.save(user);
        }
    }
}

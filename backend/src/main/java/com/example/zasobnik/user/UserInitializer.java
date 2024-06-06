package com.example.zasobnik.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserInitializer implements CommandLineRunner {
    private final UserService userService;

    @Value("${SPRING_SINGLE_LOGIN}")
    private String login;

    @Value("${SPRING_SINGLE_PASSWORD}")
    private String rawPassword;

    @Override
    public void run(String... args) throws Exception {
        userService.createUser(login, rawPassword);
    }
}

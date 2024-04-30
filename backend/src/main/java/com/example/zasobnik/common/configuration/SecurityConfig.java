package com.example.zasobnik.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${SPRING_SINGLE_LOGIN}")
    private String login;
    @Value("${SPRING_SINGLE_PASSWORD}")
    private String rawPassword;

    public static final String SERVER_API_URL = "/api/server/**";
    public static final String CLIENT_API_URL = "/api/client/**";
    public static final String PUBLIC_API_URL = "/api/public/**";

    public static String encryptDefaultPassword(String rawPassword) {
        Argon2PasswordEncoder encoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
        return "{argon2@SpringSecurity_v5_8}" + encoder.encode(rawPassword);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(PUBLIC_API_URL).authenticated()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(SERVER_API_URL)
                        .ignoringRequestMatchers(PUBLIC_API_URL));
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withUsername(login)
                .password(encryptDefaultPassword(rawPassword))
                .roles("USER")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

}

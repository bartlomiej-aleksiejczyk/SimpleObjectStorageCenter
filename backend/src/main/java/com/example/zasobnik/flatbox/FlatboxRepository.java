package com.example.zasobnik.flatbox;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlatboxRepository extends JpaRepository<Flatbox, Long> {
    boolean existsBySlug(String slug);

    Optional<Flatbox> findBySlug(String slug);
}

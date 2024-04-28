package com.example.zasobnik.flatbox;

import jakarta.validation.constraints.NotBlank;

public record FlatboxCreateDTO(@NotBlank String slug

) {
}

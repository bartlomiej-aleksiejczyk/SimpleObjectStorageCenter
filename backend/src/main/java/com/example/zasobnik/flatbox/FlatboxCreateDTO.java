package com.example.zasobnik.flatbox;

import jakarta.validation.constraints.NotBlank;

public record FlatboxCreateDTO(
                @NotBlank(message = "Slug cannot be blank") String slug

) {
}

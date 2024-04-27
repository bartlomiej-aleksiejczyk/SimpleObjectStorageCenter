package com.example.zasobnik.flatbox;

import com.example.zasobnik.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "flatboxes")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Flatbox extends BaseEntity {
    @Column(length = 255)
    String slug;
    @Enumerated(EnumType.STRING)
    private FlatboxAccessType accessType;
}

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
@Table(name = "flat_boxes")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlatBox extends BaseEntity {
    @Column(length = 255)
    String slug;
    @Enumerated(EnumType.STRING)
    private FlatBoxAccessType accessType;
}

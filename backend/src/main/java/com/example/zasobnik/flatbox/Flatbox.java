package com.example.zasobnik.flatbox;

import com.example.zasobnik.common.BaseEntity;
import com.example.zasobnik.access.FlatboxAccessPermission;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "flatboxes")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Flatbox extends BaseEntity {
    @Column(length = 255, unique = true)
    String slug;
    @Enumerated(EnumType.STRING)
    private FlatboxAccessType accessType;
    @OneToMany(mappedBy = "flatbox", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<FlatboxAccessPermission> userPermissions;
}

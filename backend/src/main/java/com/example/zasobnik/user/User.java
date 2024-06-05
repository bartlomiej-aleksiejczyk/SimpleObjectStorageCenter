package com.example.zasobnik.user;

import com.example.zasobnik.common.BaseEntity;
import com.example.zasobnik.access.FlatboxAccessPermission;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User extends BaseEntity {
    String username;
    String password;
    String roles;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<FlatboxAccessPermission> flatboxPermissions;
}

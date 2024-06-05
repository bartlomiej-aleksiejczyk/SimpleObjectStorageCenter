package com.example.zasobnik.user;

import com.example.zasobnik.common.BaseEntity;
import com.example.zasobnik.access.FlatboxAccessPermission;
import com.example.zasobnik.user.UserRole;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class User extends BaseEntity {
    String username;
    String password;
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    UserRole role;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<FlatboxAccessPermission> flatboxPermissions;

}

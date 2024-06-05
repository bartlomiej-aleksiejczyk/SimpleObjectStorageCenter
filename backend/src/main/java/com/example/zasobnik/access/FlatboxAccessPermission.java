package com.example.zasobnik.access;

import com.example.zasobnik.user.User;
import com.example.zasobnik.flatbox.Flatbox;
import com.example.zasobnik.common.BaseEntity;
import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "flatbox_access_permissions")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FlatboxAccessPermission extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flatbox_id", nullable = false)
    Flatbox flatbox;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    AccessLevel accessLevel;

    public enum AccessLevel {
        READ, EDIT
    }
}

package com.myme.mywarehome.domains.user.application.domain;

import com.myme.mywarehome.infrastructure.common.jpa.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true)
    private String id;

    private String password;

    private Role role;

    @Builder
    public User(String name, String phoneNumber, String id, String password, Role role) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.password = password;
        this.role = role;
    }

    public void changePassword(String password) {
        this.password = password;
    }
}

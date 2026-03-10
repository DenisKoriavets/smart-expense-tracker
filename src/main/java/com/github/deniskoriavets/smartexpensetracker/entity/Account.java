package com.github.deniskoriavets.smartexpensetracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class Account {
    @Id
    @GeneratedValue
    @EqualsAndHashCode.Include
    @ToString.Include
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @ToString.Include
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ToString.Include
    private AccountType type;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}

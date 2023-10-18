package com.craft.userAuth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;


import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false,unique = true)
    private String email;

    private Instant created;

    private boolean active;

}

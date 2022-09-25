package com.filip.managementapp.model;

import lombok.*;

import javax.persistence.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 123432432423312L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    private Boolean isEnabled;

    @OneToMany(
            mappedBy = "user",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.DETACH}
    )
    private List<Order> orders;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    private ShoppingCart shoppingCart;

    @ManyToMany(
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE, CascadeType.DETACH}
    )
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(Long id,
                String firstName,
                String lastName,
                String email,
                String password,
                Gender gender,
                LocalDate birthDate,
                Boolean isEnabled,
                Set<Role> roles) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isEnabled = isEnabled;
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        if (isEnabled != user.isEnabled) return false;
        if (!Objects.equals(id, user.id)) return false;
        if (!firstName.equals(user.firstName)) return false;
        if (!lastName.equals(user.lastName)) return false;
        if (!email.equals(user.email)) return false;
        if (!password.equals(user.password)) return false;
        if (gender != user.gender) return false;
        return birthDate.equals(user.birthDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + firstName.hashCode();
        result = 31 * result + lastName.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + gender.hashCode();
        result = 31 * result + birthDate.hashCode();
        result = 31 * result + (Boolean.TRUE.equals(isEnabled) ? 1 : 0);
        return result;
    }
}
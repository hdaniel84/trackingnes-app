package com.mesacer.trackingnes.trackingnes_app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String email;

    @Column
    private LocalDateTime created_at;

    @ManyToMany(fetch = FetchType.EAGER) // EAGER para cargar los roles inmediatamente con el usuario
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    // ------------------- Métodos de UserDetails (Spring Security)
    // -------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // Colección para authorities
        Set<GrantedAuthority> authorities = new java.util.HashSet<>();

        // Agregar roles
        authorities.addAll(roles);

        // Agregar privilegios de cada rol
        roles.forEach(role -> {
            authorities.addAll(role.getPrivileges());
        });

        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Column
    private boolean enabled;

    @OneToOne
    @JoinColumn(name = "id_person", nullable = false)
    private Person person;

    @Override
    public boolean isEnabled() {
        // Obligamos a que devuelva el valor de la variable de la BD
        return this.enabled; 
    }
}
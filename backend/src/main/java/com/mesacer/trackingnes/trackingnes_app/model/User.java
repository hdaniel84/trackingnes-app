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
    @JoinTable(
        name = "user_roles", 
        joinColumns = @JoinColumn(name = "user_id"), 
        inverseJoinColumns = @JoinColumn(name = "role_id") 
    )
    private Set<Role> roles;

    // ------------------- Métodos de UserDetails (Spring Security) -------------------
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
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
}
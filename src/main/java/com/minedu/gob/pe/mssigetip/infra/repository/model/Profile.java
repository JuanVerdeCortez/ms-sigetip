package com.minedu.gob.pe.mssigetip.infra.repository.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@Table(name = "Profile")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "profile_menu",
            joinColumns = @JoinColumn(name = "Profile_Id"),
            inverseJoinColumns = @JoinColumn(name = "Menu_Id")
    )
    private Set<Menu> menus;
}


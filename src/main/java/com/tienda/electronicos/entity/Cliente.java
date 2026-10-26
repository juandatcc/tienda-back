package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CLIENTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_CLIENTE")
    private String id;

    @Column(name = "NOMBRE", nullable = false)
    private String nombre;

    @Column(name = "TELEFONO")
    private String telefono;

    @Column(name = "DIRECCION")
    private String direccion;

    /**
     * Relación 1 a 1 con Usuario
     * Un usuario solo puede ser un cliente
     */
    @OneToOne(optional = false)
    @JoinColumn(
            name = "ID_USUARIO",
            nullable = false,
            unique = true
    )
    private Usuario usuario;
}

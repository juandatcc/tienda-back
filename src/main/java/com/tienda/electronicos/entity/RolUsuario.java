
package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROL_USUARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_ROL")
    private String idRol;

    /** Nombre del rol en mayúsculas: ROLE_ADMIN, ROLE_USER, ... */
    @Column(name = "NOMBRE", nullable = false, unique = true)
    private String nombre;
}

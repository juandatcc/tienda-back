
package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ROL_USUARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class RolUsuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rol")
    @SequenceGenerator(name = "seq_rol", sequenceName = "SEQ_ROL_USUARIO", allocationSize = 1)
    @Column(name = "ID_ROL")
    private Long idRol;

    /** Nombre del rol en mayúsculas: ADMIN, USER, ... */
    @Column(name = "NOMBRE", nullable = false, unique = true)
    private String nombre;
}

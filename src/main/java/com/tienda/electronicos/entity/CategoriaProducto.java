package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CATEGORIA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaProducto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "ID_CATEGORIA")
    private String idCategoria;

    @Column(name = "NOMBRE", nullable = false, unique = true)
    private String nombre;

    @Column(name = "DESCRIPCION")
    private String descripcion;
}


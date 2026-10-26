package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String idProducto;

    private String nombre;

    private String descripcion;

    @Column(name = "PRECIO", columnDefinition = "FLOAT(126)")
    private BigDecimal precio;

    private Integer stock;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoriaProducto categoria;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;
}

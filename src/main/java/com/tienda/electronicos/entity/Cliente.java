package com.tienda.electronicos.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String correo;

    private String telefono;

    private String direccion;

    /**
     * Relación 1 a 1 con Usuario
     * Un usuario solo puede ser un cliente
     */
    @OneToOne(optional = false)
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            unique = true
    )
    private Usuario usuario;

    // getters y setters



    public void setNombre() {
    }

    public void setDireccion() {
    }

    public void setUsuario() {
    }

    public void setTelefono() {
    }

    public String getNombre() {
        return null;
    }

    public String getCorreo() {
        return null;
    }

    public String getTelefono() {
        return null;
    }

    public String getDireccion() {
        return null;
    }

    public void setCorreo() {

    }
}
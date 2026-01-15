package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Carrito;
import com.tienda.electronicos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
}

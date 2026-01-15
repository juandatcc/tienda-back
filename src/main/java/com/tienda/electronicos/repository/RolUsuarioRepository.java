package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RolUsuarioRepository extends JpaRepository<RolUsuario, Long> {

    Optional<RolUsuario> findByNombre(String nombre);
}

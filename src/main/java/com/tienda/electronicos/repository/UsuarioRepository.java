package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // 🔑 MÉTODO CORRECTO
    Optional<Usuario> findByCorreo(String correo);

}


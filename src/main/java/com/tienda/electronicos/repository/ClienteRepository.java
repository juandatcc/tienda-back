package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Cliente;
import com.tienda.electronicos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByUsuario(Usuario usuario);

}

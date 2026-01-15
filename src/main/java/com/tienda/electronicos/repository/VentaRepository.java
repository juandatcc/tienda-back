package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.Venta;
import com.tienda.electronicos.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long> {

    List<Venta> findByUsuario(Usuario usuario);
}

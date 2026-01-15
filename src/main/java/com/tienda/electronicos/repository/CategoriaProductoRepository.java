
package com.tienda.electronicos.repository;

import com.tienda.electronicos.entity.CategoriaProducto;
import org.springframework.data.repository.CrudRepository;

public interface CategoriaProductoRepository
        extends CrudRepository<CategoriaProducto, Long> {

    boolean existsByNombre(String nombre);
}

package com.tienda.electronicos.service;

import com.tienda.electronicos.repository.ProductoRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;

    public long totalUsuarios() {
        return usuarioRepository.count();
    }

    public long totalProductos() {
        return productoRepository.count();
    }
}

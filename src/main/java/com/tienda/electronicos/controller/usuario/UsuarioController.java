package com.tienda.electronicos.controller.usuario;

import com.tienda.electronicos.dto.usuario.UsuarioResponse;
import com.tienda.electronicos.dto.usuario.UsuarioUpdateRequest;
import com.tienda.electronicos.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // ===== LISTAR (SOLO ADMIN) =====
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

    // ===== OBTENER POR ID (el propio usuario o ADMIN) =====
    @GetMapping("/{id}")
    public UsuarioResponse obtenerPorId(
            @PathVariable("id") String id,
            Authentication authentication
    ) {
        return usuarioService.obtenerUsuarioPorId(id, authentication);
    }

    // ===== ACTUALIZAR (el propio usuario o ADMIN) =====
    @PutMapping("/{id}")
    public UsuarioResponse actualizarUsuario(
            @PathVariable("id") String id,
            @RequestBody @Valid UsuarioUpdateRequest request,
            Authentication authentication
    ) {
        return usuarioService.actualizarUsuario(id, request, authentication);
    }
}

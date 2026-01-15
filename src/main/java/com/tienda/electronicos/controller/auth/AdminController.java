package com.tienda.electronicos.controller.auth;

import com.tienda.electronicos.dto.admin.UsuarioAdminResponse;
import com.tienda.electronicos.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UsuarioService usuarioService;

    // =========================
    // TEST ADMIN
    // =========================
    @GetMapping("/status")
    public String adminStatus() {
        return "ADMIN AUTORIZADO";
    }

    // =========================
    // LISTAR USUARIOS
    // =========================
    @GetMapping("/usuarios")
    public List<UsuarioAdminResponse> listarUsuarios() {
        return usuarioService.listarUsuarios();
    }

}

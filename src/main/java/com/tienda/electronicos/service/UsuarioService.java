package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.admin.UsuarioAdminResponse;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // =========================
    // LISTAR USUARIOS (ADMIN)
    // =========================
    public List<UsuarioAdminResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToAdminResponse)
                .toList();
    }

    // =========================
    // CONTAR USUARIOS
    // =========================
    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    // =========================
    // ELIMINAR USUARIO POR ID
    // =========================
    @Transactional
    public void eliminarUsuario(Long idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new IllegalArgumentException(
                    "Usuario no encontrado con id: " + idUsuario
            );
        }
        usuarioRepository.deleteById(idUsuario);
    }

    // =========================
    // OBTENER USUARIO POR CORREO
    // =========================
    public UsuarioAdminResponse obtenerUsuarioPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado")
                );

        return mapToAdminResponse(usuario);
    }

    // =========================
    // MAPPER PRIVADO
    // =========================
    private UsuarioAdminResponse mapToAdminResponse(Usuario usuario) {
        return new UsuarioAdminResponse(
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getNombre(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                usuario.getRol().getNombre()
        );
    }
}

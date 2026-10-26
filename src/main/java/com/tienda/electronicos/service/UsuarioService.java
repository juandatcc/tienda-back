package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.usuario.UsuarioResponse;
import com.tienda.electronicos.dto.usuario.UsuarioUpdateRequest;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    // =========================
    // LISTAR USUARIOS (ADMIN)
    // =========================
    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(this::mapToResponse)
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
    public void eliminarUsuario(String idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado con id: " + idUsuario
            );
        }
        usuarioRepository.deleteById(idUsuario);
    }

    // =========================
    // OBTENER USUARIO POR CORREO
    // =========================
    public UsuarioResponse obtenerUsuarioPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
                );

        return mapToResponse(usuario);
    }

    // =========================
    // OBTENER USUARIO POR ID (el propio usuario o ADMIN)
    // =========================
    public UsuarioResponse obtenerUsuarioPorId(String idUsuario, Authentication auth) {
        Usuario usuario = buscarYAutorizar(idUsuario, auth);
        return mapToResponse(usuario);
    }

    // =========================
    // ACTUALIZAR USUARIO (el propio usuario o ADMIN)
    // =========================
    @Transactional
    public UsuarioResponse actualizarUsuario(String idUsuario, UsuarioUpdateRequest request, Authentication auth) {
        Usuario usuario = buscarYAutorizar(idUsuario, auth);

        usuario.setNombre(request.getNombre());
        usuario.setTelefono(
                request.getTelefono() == null || request.getTelefono().isBlank() ? null : request.getTelefono()
        );
        usuario.setDireccion(request.getDireccion());

        return mapToResponse(usuarioRepository.save(usuario));
    }

    // =========================
    // AUXILIARES
    // =========================

    // Busca el usuario y verifica que el autenticado sea el mismo usuario o ADMIN (si no, 403)
    private Usuario buscarYAutorizar(String idUsuario, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado")
                );

        boolean esAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        boolean esElMismo = usuario.getCorreo().equalsIgnoreCase(auth.getName());

        if (!esAdmin && !esElMismo) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para acceder a este usuario");
        }
        return usuario;
    }

    private UsuarioResponse mapToResponse(Usuario usuario) {
        String rol = usuario.getRol() != null ? usuario.getRol().getNombre() : null;
        if (rol != null && rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getIdUsuario(),
                usuario.getCorreo(),
                usuario.getNombre(),
                usuario.getTelefono(),
                usuario.getDireccion(),
                rol,
                usuario.getFechaRegistro()
        );
    }
}

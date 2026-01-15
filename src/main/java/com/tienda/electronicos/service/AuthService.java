package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.auth.AuthResponse;
import com.tienda.electronicos.dto.auth.LoginRequest;
import com.tienda.electronicos.dto.auth.RegisterRequest;
import com.tienda.electronicos.entity.RolUsuario;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.RolUsuarioRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import com.tienda.electronicos.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthService {

    // =========================
    // DEPENDENCIAS
    // =========================
    private final UsuarioRepository usuarioRepository;
    private final RolUsuarioRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // =========================
    // LOGIN
    // =========================
    @Transactional(readOnly = true)
    // El método login recibe un LoginRequest y devuelve un AuthResponse
    public AuthResponse login(LoginRequest request) {

        // 1️⃣ Buscar usuario por correo
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuario o contraseña incorrectos"
                ));

        // 2️⃣ Verificar contraseña
        if (!passwordEncoder.matches(
                request.getContrasena(),
                usuario.getContrasena()
        )) {
            // 3️⃣ Error de autenticación
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuario o contraseña incorrectos"
            );
        }

        // 4️⃣ Generar token
        String token = jwtUtil.generarToken(
                usuario.getCorreo(),
                Collections.singleton(usuario.getRol().getNombre())
        );

        // 5️⃣ Responder con token y datos del usuario
        return new AuthResponse(
                token,
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );
    }

    // =========================
    // REGISTER
    // =========================
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // 1️⃣ Correo único
        if (usuarioRepository.findByCorreo(request.getCorreo()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo ya está registrado"
            );
        }

        // 2️⃣ Determinar rol
        String nombreRol = "ROLE_USER";
        if ("ADMIN-2025".equals(request.getCodigoAdmin())) {
            nombreRol = "ADMIN";
        }

        // 3️⃣ Buscar rol
        RolUsuario rol = rolRepository.findByNombre(nombreRol)
                .orElseThrow(() ->
                        new IllegalStateException("Rol no encontrado")
                );

        // 4️⃣ Crear usuario
        Usuario usuario = Usuario.builder()
                .correo(request.getCorreo())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .rol(rol)
                .build();

        usuarioRepository.save(usuario);

        // 5️⃣ Token
        String token = jwtUtil.generarToken(
                usuario.getCorreo(),
                Collections.singleton(rol.getNombre())
        );

        // 6️⃣ Responder con token y datos del usuario
        return new AuthResponse(
                token,
                usuario.getCorreo(),
                rol.getNombre()
        );
    }
}

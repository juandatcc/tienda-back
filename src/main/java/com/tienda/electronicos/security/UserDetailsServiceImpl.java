package com.tienda.electronicos.security;

import com.tienda.electronicos.entity.RolUsuario;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
// Implementación personalizada de UserDetailsService para cargar detalles del usuario desde la base de datos
public class UserDetailsServiceImpl implements UserDetailsService {

    // Repositorio para acceder a los datos de los usuarios
    private final UsuarioRepository usuarioRepository;

    // Constructor que inyecta el repositorio de usuarios
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Carga los detalles del usuario por su nombre de usuario (correo electrónico)
    @Override
    @Transactional(readOnly = true)
    // Método para cargar los detalles del usuario
    public UserDetails loadUserByUsername(String correo)
            throws UsernameNotFoundException {

        // Buscar el usuario en la base de datos por su correo electrónico
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado")
                );

        // Obtener el rol del usuario
        RolUsuario rol = usuario.getRol();
        if (rol == null || rol.getNombre() == null) {
            throw new BadCredentialsException("El usuario no tiene un rol válido");
        }

        // Formatear el nombre del rol para cumplir con el estándar de Spring Security
        String rolNombre = rol.getNombre().toUpperCase();
        if (rolNombre.startsWith("ROLE_")) {
            rolNombre = rolNombre.substring(5);
        }

        // Crear la lista de autoridades (roles) del usuario
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + rolNombre)
        );

        // Devolver un objeto UserDetails con la información del usuario
        return new org.springframework.security.core.userdetails.User(
                usuario.getCorreo(),
                usuario.getContrasena(),
                true,
                true,
                true,
                true,
                authorities
        );
    }
}

package com.tienda.electronicos.service;

import com.tienda.electronicos.dto.cliente.ClienteRequest;
import com.tienda.electronicos.dto.cliente.ClienteResponse;
import com.tienda.electronicos.entity.Cliente;
import com.tienda.electronicos.entity.Usuario;
import com.tienda.electronicos.repository.ClienteRepository;
import com.tienda.electronicos.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClienteService {

    // Repositorios
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;

    // ===============================
    // LISTAR TODOS LOS CLIENTES
    // ===============================
    @Transactional(readOnly = true)
    public List<ClienteResponse> listarClientes() {
        return clienteRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // ===============================
    // OBTENER CLIENTE POR ID
    // ===============================
    @Transactional(readOnly = true)
    public ClienteResponse obtenerClientePorId(String idCliente) {
        return toResponse(buscar(idCliente));
    }

    // ===============================
    // CREAR CLIENTE
    // ===============================
    public ClienteResponse crearCliente(ClienteRequest request) {
        if (request.getIdUsuario() == null || request.getIdUsuario().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El usuario es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (clienteRepository.findByUsuario(usuario).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya tiene un cliente asociado");
        }

        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .direccion(request.getDireccion())
                .usuario(usuario)
                .build();

        return toResponse(clienteRepository.save(cliente));
    }

    // ===============================
    // ACTUALIZAR CLIENTE
    // ===============================
    public ClienteResponse actualizarCliente(String idCliente, ClienteRequest request) {
        Cliente cliente = buscar(idCliente);

        cliente.setNombre(request.getNombre());
        cliente.setTelefono(request.getTelefono());
        cliente.setDireccion(request.getDireccion());

        return toResponse(clienteRepository.save(cliente));
    }

    // ===============================
    // ELIMINAR CLIENTE
    // ===============================
    public void eliminarCliente(String idCliente) {

        if (!clienteRepository.existsById(idCliente)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + idCliente);
        }

        clienteRepository.deleteById(idCliente);
    }

    // ===============================
    // CONTAR CLIENTES
    // ===============================
    @Transactional(readOnly = true)
    public long contarClientes() {
        return clienteRepository.count();
    }

    // ===============================
    // AUXILIARES
    // ===============================
    private Cliente buscar(String idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado con id: " + idCliente)
                );
    }

    private ClienteResponse toResponse(Cliente c) {
        Usuario u = c.getUsuario();
        return new ClienteResponse(
                c.getId(),
                c.getNombre(),
                c.getDireccion(),
                c.getTelefono(),
                u != null ? u.getIdUsuario() : null,
                u != null ? u.getCorreo() : null
        );
    }
}

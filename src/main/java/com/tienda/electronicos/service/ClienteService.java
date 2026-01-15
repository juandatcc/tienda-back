package com.tienda.electronicos.service;

import com.tienda.electronicos.entity.Cliente;
import com.tienda.electronicos.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    // Repositorio de Cliente
    private final ClienteRepository clienteRepository;

    // ===============================
    // LISTAR TODOS LOS CLIENTES
    // ===============================
    @GetMapping
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    // ===============================
    // OBTENER CLIENTE POR ID
    // ===============================
    public Cliente obtenerClientePorId(Long idCliente) {
        return clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado con id: " + idCliente)
                );
    }

    // ===============================
    // CREAR CLIENTE
    // ===============================
    @PostMapping
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    // ===============================
    // ACTUALIZAR CLIENTE
    // ===============================
    @PutMapping
    public Cliente actualizarCliente(Long idCliente, Cliente clienteActualizado) {

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() ->
                        new RuntimeException("Cliente no encontrado con id: " + idCliente)
                );

        cliente.setNombre(clienteActualizado.getNombre());
        cliente.setNombre(clienteActualizado.getCorreo());
        cliente.setTelefono(clienteActualizado.getTelefono());
        cliente.setDireccion(clienteActualizado.getDireccion());

        return clienteRepository.save(cliente);
    }

    // ===============================
    // ELIMINAR CLIENTE
    // ===============================
    @DeleteMapping
    public void eliminarCliente(Long idCliente) {

        if (!clienteRepository.existsById(idCliente)) {
            throw new RuntimeException("Cliente no encontrado con id: " + idCliente);
        }

        clienteRepository.deleteById(idCliente);
    }

    // ===============================
    // CONTAR CLIENTES
    // ===============================
    public long contarClientes() {
        return clienteRepository.count();
    }
}

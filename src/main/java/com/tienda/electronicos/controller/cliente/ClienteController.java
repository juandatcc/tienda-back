package com.tienda.electronicos.controller.cliente;

import com.tienda.electronicos.dto.cliente.ClienteRequest;
import com.tienda.electronicos.dto.cliente.ClienteResponse;
import com.tienda.electronicos.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ClienteController {

    private final ClienteService clienteService;

    // ===============================
    // LISTAR TODOS LOS CLIENTES
    // ===============================
    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    // ===============================
    // OBTENER CLIENTE POR ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<ClienteResponse> obtenerClientePorId(@PathVariable("id") String id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }

    // ===============================
    // CREAR CLIENTE
    // ===============================
    @PostMapping
    public ResponseEntity<ClienteResponse> crearCliente(@RequestBody @Valid ClienteRequest request) {
        return ResponseEntity.ok(clienteService.crearCliente(request));
    }

    // ===============================
    // ACTUALIZAR CLIENTE
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<ClienteResponse> actualizarCliente(
            @PathVariable("id") String id,
            @RequestBody @Valid ClienteRequest request
    ) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, request));
    }

    // ===============================
    // ELIMINAR CLIENTE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable("id") String id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================
    // CONTAR CLIENTES
    // ===============================
    @GetMapping("/count")
    public ResponseEntity<Long> contarClientes() {
        return ResponseEntity.ok(clienteService.contarClientes());
    }
}

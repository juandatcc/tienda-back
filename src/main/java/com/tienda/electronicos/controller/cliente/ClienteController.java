package com.tienda.electronicos.controller.cliente;

import com.tienda.electronicos.entity.Cliente;
import com.tienda.electronicos.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    // ===============================
    // LISTAR TODOS LOS CLIENTES
    // ===============================
    @GetMapping
    public ResponseEntity<List<Cliente>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    // ===============================
    // OBTENER CLIENTE POR ID
    // ===============================
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerClientePorId(id));
    }

    // ===============================
    // CREAR CLIENTE
    // ===============================
    @PostMapping
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(clienteService.crearCliente(cliente));
    }

    // ===============================
    // ACTUALIZAR CLIENTE
    // ===============================
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(
            @PathVariable Long id,
            @RequestBody Cliente cliente
    ) {
        return ResponseEntity.ok(clienteService.actualizarCliente(id, cliente));
    }

    // ===============================
    // ELIMINAR CLIENTE
    // ===============================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
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

package com.tienda.electronicos.security;

import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
// Servicio para gestionar tokens inválidos (lista negra)
public class TokenBlacklistService {

    // Conjunto concurrente para almacenar tokens inválidos
    private final Set<String> blacklistedTokens =
            ConcurrentHashMap.newKeySet();

    // Método para invalidar un token
    public void invalidate(String token) {
        blacklistedTokens.add(token);
    }

    // Método para verificar si un token es inválido
    public boolean isInvalid(String token) {
        return blacklistedTokens.contains(token);
    }
}

package com.tienda.electronicos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // hace component scan desde com.tienda.electronicos hacia abajo
public class ElectronicosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ElectronicosApplication.class, args);
    }
}

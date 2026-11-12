package com.bootcamp.directorio.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Endpoint que usa el formulario de login para validar las credenciales (HTTP Basic). */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, String> me(Authentication authentication) {
        return Map.of("usuario", authentication.getName());
    }
}

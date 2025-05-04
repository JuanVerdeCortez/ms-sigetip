package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.entity.LoginRequest;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import com.minedu.gob.pe.mssigetip.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UsuarioControllerTest {

    @Mock
    private UserService usuarioService;

    @InjectMocks
    private UsuarioController usuarioController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAutenticarUsuarioSuccess() {
        // Configuración de datos de prueba
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        User usuario = new User();
        usuario.setId(1L);
        usuario.setUsername("testuser");

        // Configuración del mock
        when(usuarioService.autenticarUsuario("testuser", "password123")).thenReturn(Optional.of(usuario));

        // Ejecución del método
        ResponseEntity<?> response = usuarioController.autenticarUsuario(loginRequest);

        // Verificaciones
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(usuario, response.getBody());
        verify(usuarioService, times(1)).autenticarUsuario("testuser", "password123");
    }

    @Test
    void testAutenticarUsuarioFailure() {
        // Configuración de datos de prueba
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername("wronguser");
        loginRequest.setPassword("wrongpassword");

        // Configuración del mock
        when(usuarioService.autenticarUsuario("wronguser", "wrongpassword")).thenReturn(Optional.empty());

        // Ejecución del método
        ResponseEntity<?> response = usuarioController.autenticarUsuario(loginRequest);

        // Verificaciones
        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Credenciales inválidas", response.getBody());
        verify(usuarioService, times(1)).autenticarUsuario("wronguser", "wrongpassword");
    }
}

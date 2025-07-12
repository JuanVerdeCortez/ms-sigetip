package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.infra.repository.model.RegistroTitulo;
import com.minedu.gob.pe.mssigetip.service.RegistroTituloService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.*;

class RegistroTituloControllerTest {

    @Mock
    private RegistroTituloService registroTituloService;

    @InjectMocks
    private RegistroTituloController registroTituloController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /*@Test
    void testCreateRegistroTitulo() throws IOException {
        RegistroTitulo registroTitulo = new RegistroTitulo();
        registroTitulo.setId(1L);
        registroTitulo.setEstado(1);

        when(registroTituloService.saveRegistroTitulo(any(RegistroTitulo.class))).thenReturn(registroTitulo);

        ResponseEntity<RegistroTitulo> response = registroTituloController.createRegistroTitulo(registroTitulo);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(registroTitulo, response.getBody());
        verify(registroTituloService, times(1)).saveRegistroTitulo(any(RegistroTitulo.class));
    }*/

    @Test
    void testGetAllRegistros() {
        List<RegistroTitulo> registros = new ArrayList<>();
        RegistroTitulo registro1 = new RegistroTitulo();
        RegistroTitulo registro2 = new RegistroTitulo();
        registros.add(registro1);
        registros.add(registro2);

        when(registroTituloService.getAllRegistros()).thenReturn(registros);

        ResponseEntity<List<RegistroTitulo>> response = registroTituloController.getAllRegistros();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(registroTituloService, times(1)).getAllRegistros();
    }

    @Test
    void testGetRegistroById() {
        RegistroTitulo registroTitulo = new RegistroTitulo();
        registroTitulo.setId(1L);

        when(registroTituloService.getRegistroById(1L)).thenReturn(registroTitulo);

        ResponseEntity<RegistroTitulo> response = registroTituloController.getRegistroById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(registroTitulo, response.getBody());
        verify(registroTituloService, times(1)).getRegistroById(1L);
    }
}


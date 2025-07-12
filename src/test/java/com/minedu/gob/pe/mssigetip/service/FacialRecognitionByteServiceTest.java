package com.minedu.gob.pe.mssigetip.service;

import com.minedu.gob.pe.mssigetip.infra.repository.UserRepository;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class FacialRecognitionByteServiceTest {
    @InjectMocks
    private FacialRecognitionByteService facialRecognitionByteService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        facialRecognitionByteService = new FacialRecognitionByteService();
    }

    @Test
    void compareFaces() {
        byte[] image1 = "test image 1".getBytes();
        byte[] image2 = "test image 2".getBytes();

        boolean result = facialRecognitionByteService.compareFaces(image1, image2);

        assertFalse(result);
    }

    @Test
    void obtenerFoto() {
        String username = "testUser";
        byte[] expectedPhoto = "test photo".getBytes();
        User user = new User();
        user.setFoto(expectedPhoto);
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        ReflectionTestUtils.setField(facialRecognitionByteService, "userRepository", userRepository);

        byte[] result = facialRecognitionByteService.obtenerFoto(username);

        assertArrayEquals(expectedPhoto, result);
    }

}
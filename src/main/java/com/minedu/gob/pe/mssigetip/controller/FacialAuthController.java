package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.entity.FacialAuthRequest;
import com.minedu.gob.pe.mssigetip.service.FacialRecognitionByteService;
import com.minedu.gob.pe.mssigetip.service.FacialRecognitionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/facial-auth")
@CrossOrigin(origins = "http://localhost:4200")
public class FacialAuthController {

    @Autowired
    private FacialRecognitionByteService facialRecognitionByteService;

    @PostMapping("/api/facial/upload")
    public ResponseEntity<String> uploadImage(@RequestBody Map<String, String> payload) {
        try {
            String base64Image = payload.get("imageBase64");
            // Eliminar el prefijo: data:image/jpeg;base64,
            base64Image = base64Image.split(",")[1];

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);

            Path outputPath = Paths.get("C:/fotos_usuarios/usuario123/registro.jpg");
            Files.createDirectories(outputPath.getParent());
            Files.write(outputPath, imageBytes);

            return ResponseEntity.ok("Imagen guardada exitosamente");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar imagen");
        }


    }


    /*private final String REGISTERED_IMAGE_PATH = "src/main/resources/opencv/sho.jpeg";
    private final String TEMP_IMAGE_PATH = "src/main/resources/opencv/sho.jpeg";

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        try {
            // Guardar temporalmente la imagen recibida
            Path tempPath = new File(TEMP_IMAGE_PATH).toPath();
            Files.copy(file.getInputStream(), tempPath, StandardCopyOption.REPLACE_EXISTING);

            boolean isMatch = FacialRecognitionService.authenticate(REGISTERED_IMAGE_PATH, TEMP_IMAGE_PATH);

            return ResponseEntity.ok().body("{\"match\": " + isMatch + "}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar la imagen");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }*/

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestParam("file") MultipartFile file, @RequestParam("username") String username) {
        try {
            // Conversión de la imagen enviada a bytes
            byte[] receivedImageBytes = file.getBytes();
            log.error(" receivedImageBytes " + receivedImageBytes + " username " + username);
            // Obtener la imagen registrada de la base de datos
            byte[] registeredImageBytes = facialRecognitionByteService.obtenerFoto(username);
            log.error(" registeredImageBytes " + registeredImageBytes);
            // Comparar las imágenes (puedes usar OpenCV en lugar de Arrays.equals para reconocimiento facial)
            boolean isMatch = facialRecognitionByteService.compareFaces(receivedImageBytes, registeredImageBytes);

            // Retornar la respuesta
            return ResponseEntity.ok().body("{\"match\": " + isMatch + "}");

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar la imagen");
        }
    }


}

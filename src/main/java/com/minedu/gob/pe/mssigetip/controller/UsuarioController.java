package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.entity.LoginRequest;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import com.minedu.gob.pe.mssigetip.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/usuario")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UserService userService;

    @PostMapping("/autenticar")
    public ResponseEntity<?> autenticarUsuario(@RequestBody LoginRequest request) {
        Optional<User> usuario = userService.autenticarUsuario(request.getUsername(), request.getPassword());
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAll() {
        return ResponseEntity.ok(userService.listAll().stream().filter(u -> u.getEstado() == 1).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(
            @RequestParam("username") String username,
            @RequestParam("nombresCompletos") String nombresCompletos,
            @RequestParam("password") String password,
            @RequestParam("profileId") Long profileId,
            @RequestParam(value = "foto", required = false) MultipartFile foto,
            @RequestParam("estado") Integer estado
    ) {

        User user = new User();
        user.setUsername(username);
        user.setNombresCompletos(nombresCompletos);
        user.setContrasenia(password);
        user.setEstado(estado);
        // Procesar la foto si se envió
        if (foto != null && !foto.isEmpty()) {
            try {
                user.setFoto(foto.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        User created = userService.create(user, profileId);
        return ResponseEntity.ok(created);
    }


    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(
            @RequestParam("id") Long id,
            @RequestParam("username") String username,
            @RequestParam("nombresCompletos") String nombresCompletos,
            @RequestParam("password") String password,
            @RequestParam("profileId") Long profileId,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setNombresCompletos(nombresCompletos);
        user.setContrasenia(password);
        if (foto != null && !foto.isEmpty()) {
            try {
                user.setFoto(foto.getBytes());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        log.error("" +user);
        User updated = userService.update(user, profileId);
        return ResponseEntity.ok(updated);
    }

    // Editar usuario
    @PostMapping("/{inactive}")
    public ResponseEntity<User> update(
            @RequestParam Long id,
            @RequestParam Integer estado
    ) {
        User updated = userService.inactive(id, estado);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/recuperar-contrasenia")
    public ResponseEntity<String> recuperarContrasenia( @RequestParam("username") String username,
                                                        @RequestParam("lastPassword") String lastPassword,
                                                        @RequestParam("password") String contrasenia) {
        var user = userService.findByUsernameAndContrasenia(username, lastPassword);
        if (user.isPresent()) {
            boolean actualizado = userService.actualizarContrasenia(username, contrasenia);
            if (actualizado) {
                return ResponseEntity.ok("Contraseña actualizada correctamente.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar la contraseña.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado.");
        }

    }


}


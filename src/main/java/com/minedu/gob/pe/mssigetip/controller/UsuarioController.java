package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.entity.LoginRequest;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import com.minedu.gob.pe.mssigetip.service.UserService;
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
        return ResponseEntity.ok(userService.listAll());
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
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {

        User user = new User();
        user.setUsername(username);
        user.setNombresCompletos(nombresCompletos);
        user.setContrasenia(password);

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

    // Editar usuario
    @PutMapping("/{id}")
    public ResponseEntity<User> update(
            @PathVariable Long id,
            @RequestBody User user,
            @RequestParam(required = false) Long profileId
    ) {
        User updated = userService.update(id, user, profileId);
        return ResponseEntity.ok(updated);
    }
}


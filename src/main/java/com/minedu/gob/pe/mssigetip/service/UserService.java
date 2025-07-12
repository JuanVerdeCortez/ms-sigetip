package com.minedu.gob.pe.mssigetip.service;

import com.minedu.gob.pe.mssigetip.infra.repository.model.Profile;
import com.minedu.gob.pe.mssigetip.infra.repository.model.User;
import com.minedu.gob.pe.mssigetip.infra.repository.ProfileRepository;
import com.minedu.gob.pe.mssigetip.infra.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;

    public Optional<User> autenticarUsuario(String username, String password) {
        Optional<User> usuario = userRepository.findByUsername(username);
        if (usuario.isPresent() && usuario.get().getContrasenia().equals(password)) {
            return usuario;
        }
        return Optional.empty();
    }


    public List<User> listAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsernameAndContrasenia(String username, String contrasenia) {
        return userRepository.findByUsernameAndContrasenia(username, contrasenia);
    }

    @Transactional
    public User create(User user, Long profileId) {
        Profile profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));
        user.setProfile(profile);
        user.setCreadoEn(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Transactional
    public User update(User dto, Long profileId) {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        //user.setContrasenia(dto.getContrasenia());
        user.setUsername(dto.getUsername());
        user.setNombresCompletos(dto.getNombresCompletos());
        user.setFoto(dto.getFoto());
        user.setActualizadoEn(LocalDateTime.now());
        if (profileId != null) {
            Profile profile = profileRepository.findById(profileId)
                    .orElseThrow(() -> new IllegalArgumentException("Perfil no encontrado"));
            user.setProfile(profile);
        }
        return userRepository.save(user);
    }

    @Transactional
    public User inactive(Long id, Integer estado) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        log.debug("estado " + estado);
        user.setEstado(estado);
        user.setActualizadoEn(LocalDateTime.now());
        return userRepository.save(user);
    }

    public boolean actualizarContrasenia(String username, String nuevaContrasenia) {
        Optional<User> usuarioOpt = userRepository.findByUsername(username);
        if (usuarioOpt.isPresent()) {
            User usuario = usuarioOpt.get();
            usuario.setContrasenia(nuevaContrasenia); // De preferencia encriptar
            userRepository.save(usuario);
            return true;
        }
        return false;
    }
}


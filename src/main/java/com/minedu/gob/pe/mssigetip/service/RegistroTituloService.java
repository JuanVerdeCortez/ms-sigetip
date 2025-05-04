package com.minedu.gob.pe.mssigetip.service;

import com.minedu.gob.pe.mssigetip.infra.repository.model.RegistroTitulo;
import com.minedu.gob.pe.mssigetip.infra.repository.EgresadoRepository;
import com.minedu.gob.pe.mssigetip.infra.repository.RegistroTituloRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Transactional
public class RegistroTituloService {

    private final RegistroTituloRepository registroTituloRepository;
    private final EgresadoRepository egresadoRepository;

    public RegistroTituloService(RegistroTituloRepository registroTituloRepository, EgresadoRepository egresadoRepository) {
        this.registroTituloRepository = registroTituloRepository;
        this.egresadoRepository = egresadoRepository;
    }

    public RegistroTitulo saveRegistroTitulo(RegistroTitulo registroTitulo) {

        if (registroTitulo.getLstEgresados() != null) {
            registroTitulo.getLstEgresados().forEach(egresado -> egresado.setRegistroTitulo(registroTitulo));
        }
        return registroTituloRepository.save(registroTitulo);
    }

    public List<RegistroTitulo> getAllRegistros() {
        return registroTituloRepository.findAll();
    }

    public RegistroTitulo getRegistroById(Long id) {
        return registroTituloRepository.findById(id).orElseThrow(() -> new RuntimeException("Registro no encontrado"));
    }

    public void deleteRegistro(Long id) {
        registroTituloRepository.deleteById(id);
    }

    public List<RegistroTitulo> buscarPorCriterios(LocalDate fechaInicio, LocalDate fechaFin, Integer estado, String anio) {
        log.error("buscar " + fechaInicio + " " + fechaFin + " estado " + estado + " anio " + anio);
        return registroTituloRepository.findByFechaEstadoAnio(fechaInicio, fechaFin, estado, anio);
    }

}

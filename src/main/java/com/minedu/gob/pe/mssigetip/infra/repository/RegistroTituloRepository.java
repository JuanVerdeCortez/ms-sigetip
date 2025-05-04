package com.minedu.gob.pe.mssigetip.infra.repository;

import com.minedu.gob.pe.mssigetip.infra.repository.model.RegistroTitulo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDate;

public interface RegistroTituloRepository extends JpaRepository<RegistroTitulo, Long> {

    @Query("SELECT r FROM RegistroTitulo r " +
            "WHERE r.fechaResolucion BETWEEN :fechaInicio AND :fechaFin " +
            "AND (:estado IS NULL OR :estado = 0 OR r.estado = :estado) " +
            "AND (:anio IS NULL OR :anio = '' OR FUNCTION('YEAR', r.fechaResolucion) = :anio)")
    List<RegistroTitulo> findByFechaEstadoAnio(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin,
            @Param("estado") Integer estado,
            @Param("anio") String anio
    );


}
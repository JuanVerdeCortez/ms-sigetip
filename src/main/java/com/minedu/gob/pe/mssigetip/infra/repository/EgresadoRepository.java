package com.minedu.gob.pe.mssigetip.infra.repository;

import com.minedu.gob.pe.mssigetip.infra.repository.model.Graduate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EgresadoRepository extends JpaRepository<Graduate, Long> {
}
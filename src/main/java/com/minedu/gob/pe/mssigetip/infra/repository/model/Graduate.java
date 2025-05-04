package com.minedu.gob.pe.mssigetip.infra.repository.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "graduate")
public class Graduate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "INSTITUCION", nullable = false)
    private String institucion;

    @Column(name = "DRE", nullable = false)
    private String dre;

    @Column(name = "NUMERO_SERIE", nullable = false)
    private String numeroSerie;

    @Column(name = "FECHA_REGISTRO_DRE", nullable = false)
    private LocalDate fechaRegistroDre;

    @Column(name = "TIPO_DOCUMENTO", nullable = false)
    private String tipoDocumento;

    @Column(name = "NUMERO_DOCUMENTO", nullable = false)
    private String numeroDocumento;

    @Column(name = "NOMBRES", nullable = false)
    private String nombres;

    @Column(name = "APELLIDO_PATERNO", nullable = false)
    private String apellidoPaterno;

    @Column(name = "APELLIDO_MATERNO", nullable = false)
    private String apellidoMaterno;

    @Column(name = "SEXO", nullable = false)
    private String sexo;

    @Column(name = "CARRERA", nullable = false)
    private String carrera;

    @Column(name = "NUMERO_REGISTRO_INSTITUCIONAL", nullable = false)
    private String numeroRegistroInstitucional;

    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "TIPO_TITULO", nullable = false)
    private String tipoTitulo;

    @Lob
    @Column(name = "ARCHIVO")
    private byte[] archivo;

    @Column(name = "COMENTARIOS")
    private String comentarios;

    @ManyToOne
    @JoinColumn(name = "REGISTRO_TITULO_ID", nullable = false)
    @JsonBackReference
    private RegistroTitulo registroTitulo;
}

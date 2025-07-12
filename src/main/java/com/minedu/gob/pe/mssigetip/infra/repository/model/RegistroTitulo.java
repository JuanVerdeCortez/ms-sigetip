package com.minedu.gob.pe.mssigetip.infra.repository.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "register_title")
public class RegistroTitulo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Cambiar a IDENTITY
    private Long id;

    @Column(name = "NUMERO_RESOLUCION", nullable = false)
    private String numeroResolucion;

    @Column(name = "FECHA_RESOLUCION", nullable = false)
    private LocalDate fechaResolucion;

    @Column(name = "TIPO_SOLICITUD", nullable = false)
    private String tipoSolicitud;

    @Column(name = "DESCRIPCION", nullable = false)
    private String descripcion;

    @Column(name = "ESTADO", nullable = false)
    private Integer estado;

    @Column(name = "CREATED_BY")
    private String createdBy;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_BY")
    private String updatedBy;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @Lob
    @Column(name = "ARCHIVO")
    private byte[] archivo;

    @OneToMany(mappedBy = "registroTitulo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Graduate> lstEgresados;

    @Transient
    private String estadoDesc;

    public String getEstadoDesc() {
        switch (estado) {
            case 1:
                estadoDesc = "REGISTRADO";
                break;
            case 2:
                estadoDesc = "APROBADO";
                break;
            case 3:
                estadoDesc = "OBSERVADO";
                break;
            default:
                estadoDesc = "EN PROCESO";
                break;
        }

        return estadoDesc;
    }
}


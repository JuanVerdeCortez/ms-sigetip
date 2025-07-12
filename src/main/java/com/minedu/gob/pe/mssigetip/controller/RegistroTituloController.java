package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.infra.repository.model.Graduate;
import com.minedu.gob.pe.mssigetip.infra.repository.model.RegistroTitulo;
import com.minedu.gob.pe.mssigetip.service.RegistroTituloService;
import com.minedu.gob.pe.mssigetip.util.PDFSigner;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@RestController
@RequestMapping("/api/registro-titulo")
@CrossOrigin(origins = "http://localhost:4200")
public class RegistroTituloController {

    private final RegistroTituloService registroTituloService;

    public RegistroTituloController(RegistroTituloService registroTituloService) {
        this.registroTituloService = registroTituloService;
    }

    @PostMapping("/create")
    public ResponseEntity<RegistroTitulo> createRegistroTitulo(@RequestBody RegistroTitulo registroTitulo) throws IOException {
        registroTitulo.setCreatedAt(LocalDateTime.now());
        byte[] firmado = PDFSigner.firmarDocumento(registroTitulo.getArchivo(), registroTitulo.getCreatedBy());
        registroTitulo.setArchivo(firmado);
        log.error("firmado " + firmado);
        return ResponseEntity.ok(registroTituloService.saveRegistroTitulo(registroTitulo));
    }

    @GetMapping("getAll")
    public ResponseEntity<List<RegistroTitulo>> getAllRegistros() {
        return ResponseEntity.ok(registroTituloService.getAllRegistros());
    }

    @GetMapping("getAllInit")
    public ResponseEntity<List<RegistroTitulo>> getAllRegistrosOrdered() {
        return ResponseEntity.ok(registroTituloService.getAllRegistros().stream().filter(f -> f.getUpdatedAt() != null).sorted(Comparator.comparing(RegistroTitulo::getUpdatedAt).reversed()).limit(5).collect(Collectors.toList()));
    }


    @GetMapping("getAllGraduates")
    public ResponseEntity<List<Graduate>> getAllGraduates() {
        return ResponseEntity.ok(registroTituloService.getAllRegistros().stream().flatMap(v -> v.getLstEgresados().stream()).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegistroTitulo> getRegistroById(@PathVariable Long id) {
        return ResponseEntity.ok(registroTituloService.getRegistroById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRegistro(@PathVariable Long id) {
        registroTituloService.deleteRegistro(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/update")
    public ResponseEntity<RegistroTitulo> updateRegistroTitulo(@RequestParam Long id,
                                                               @RequestParam Integer estado,
                                                               @RequestParam String usuario) {
        RegistroTitulo registroTitulo = registroTituloService.getRegistroById(id);
        if (registroTitulo == null) {
            return ResponseEntity.notFound().build();
        }
        registroTitulo.setEstado(estado);
        registroTitulo.setUpdatedBy(usuario);
        registroTitulo.setUpdatedAt(LocalDateTime.now());
        RegistroTitulo updatedRegistro = registroTituloService.saveRegistroTitulo(registroTitulo);

        return ResponseEntity.ok(updatedRegistro);
    }

    /**
     * Endpoint para buscar registros de títulos por criterios: rango de fechas, estado y año.
     *
     * @param fechaInicio La fecha inicial del rango (formato yyyy-MM-dd)
     * @param fechaFin La fecha final del rango (formato yyyy-MM-dd)
     * @param estado El estado del registro (p. ej., 1 = REGISTRADO)
     * @param anio El año de la fecha de resolución
     * @return Lista de registros que coinciden con los criterios
     */
    @GetMapping("/buscarPorCriterios")
    public List<RegistroTitulo> buscarPorCriterios(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(required = false) Integer estado,
            @RequestParam(required = false) String anio
    ) {
        LocalDate inicio = LocalDate.parse(fechaInicio);
        LocalDate fin = LocalDate.parse(fechaFin);

        return registroTituloService.buscarPorCriterios(inicio, fin, estado, anio);
    }

}

package com.minedu.gob.pe.mssigetip.controller;

import com.minedu.gob.pe.mssigetip.infra.repository.model.RegistroTitulo;
import com.minedu.gob.pe.mssigetip.service.RegistroTituloService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/registro-titulo")
@CrossOrigin(origins = "http://localhost:4200")
public class RegistroTituloController {

    private final RegistroTituloService registroTituloService;

    public RegistroTituloController(RegistroTituloService registroTituloService) {
        this.registroTituloService = registroTituloService;
    }

    @PostMapping("/create")
    public ResponseEntity<RegistroTitulo> createRegistroTitulo(@RequestBody RegistroTitulo registroTitulo) {
        return ResponseEntity.ok(registroTituloService.saveRegistroTitulo(registroTitulo));
    }

    @GetMapping("getAll")
    public ResponseEntity<List<RegistroTitulo>> getAllRegistros() {
        return ResponseEntity.ok(registroTituloService.getAllRegistros());
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
    public ResponseEntity<RegistroTitulo> createRegistroTitulo(@RequestParam Long id,@RequestParam Integer estado) {
        RegistroTitulo registroTitulo = registroTituloService.getRegistroById(id);
        if (registroTitulo == null) {
            return ResponseEntity.notFound().build();
        }
        registroTitulo.setEstado(estado);
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

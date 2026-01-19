package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository repository;

    @Transactional
    @PostMapping//ResponseEntity entidad que devuelve codigo HTTP
    public ResponseEntity registrar(@RequestBody @Valid DatosRegistroMedico datos, UriComponentsBuilder uriComponentsBuilder) {
        var medico = new Medico(datos);
        repository.save(medico);

        var uri = uriComponentsBuilder.path("/medicos/{id}").buildAndExpand(medico.getId()).toUri();
//devolvemos codigo201, body() registro del medico, headerlocation URI donde esta el nuevo objeto tiene el id
        return ResponseEntity.created(uri).body(new DatosDetalleMedico(medico));
    }

    @GetMapping
//ResponseEntity puede responder con tipo de dato, en este caso Page
    public ResponseEntity<Page<DatosListaMedico>> listar(@PageableDefault(size=10, sort={"nombre"}) Pageable paginacion) {

        //        se guarda page en variable
        var page = repository.findAllByActivoTrue(paginacion).map(DatosListaMedico::new);
//devuelve un 200 y dentro el page
        return ResponseEntity.ok(page);
    }

    @Transactional
    @PutMapping    public ResponseEntity actualizar(@RequestBody @Valid DatosActualizacionMedico datos) {
        var medico = repository.getReferenceById(datos.id());
        medico.actualizarInformaciones(datos);
//retornamos un ok, con Datos completos dle medico actualizado en base a DTO
        return ResponseEntity.ok(new DatosDetalleMedico(medico));
    }

    @Transactional
    @DeleteMapping("/{id}")
//ResponseEntity entidad que devuelve codigo HTTP
    public ResponseEntity eliminar(@PathVariable Long id) {
//        busca el medico por el id
        var medico = repository.getReferenceById(id);
//        elimina el registro del medico
        medico.eliminar();
//Debemos retorna response entity, nocontent() responde con 204 peticion exitosa pero sin contenido
//build() construye y finaliza el objeto ResponseEntity
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity detallar(@PathVariable Long id) {
        var medico = repository.getReferenceById(id);

        return ResponseEntity.ok(new DatosDetalleMedico(medico));
    }
}

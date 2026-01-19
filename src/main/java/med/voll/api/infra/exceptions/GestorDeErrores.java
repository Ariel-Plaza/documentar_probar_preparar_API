package med.voll.api.infra.exceptions;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// Indica a Spring que esta clase manejará errores de TODOS los controllers REST
@RestControllerAdvice
public class GestorDeErrores {

    // ===== MANEJO DE ERROR 404 (No Encontrado) =====

    // Captura la excepción cuando no se encuentra una entidad en la BD
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity getionarError404() {
        // Retorna una respuesta HTTP 404 sin cuerpo
        return ResponseEntity.notFound().build();
    }

    // ===== MANEJO DE ERROR 400 (Validación Fallida) =====

    // Captura errores cuando los datos enviados no cumplen las validaciones
    // (por ejemplo: @NotNull, @Email, @Size, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity getionarError400(MethodArgumentNotValidException ex) {

        // Extrae la lista de campos que fallaron la validación
        var errores = ex.getFieldErrors();

        // Retorna HTTP 400 con un JSON que contiene:
        // - El campo que falló
        // - El mensaje de error de cada campo
        // Convierte cada FieldError en un DatosErrorValidacion
        return ResponseEntity.badRequest()
                .body(errores.stream()
                        .map(DatosErrorValidacion::new)
                        .toList());
    }

    // ===== RECORD PARA ESTRUCTURAR LA RESPUESTA DE ERROR =====

    // Record que define la estructura del JSON de respuesta
    // Ejemplo: {"campo": "email", "mensaje": "no debe estar vacío"}
    public record DatosErrorValidacion(String campo, String mensaje) {

        // Constructor alternativo que recibe un FieldError de Spring
        // y extrae automáticamente el campo y mensaje
        public DatosErrorValidacion(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
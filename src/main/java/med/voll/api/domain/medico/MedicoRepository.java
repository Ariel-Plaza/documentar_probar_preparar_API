package med.voll.api.domain.medico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    Page<Medico> findByActivoTrue(Pageable paginacion);

    // Query para verificar si un médico está activo
    @Query("SELECT m.activo FROM Medico m WHERE m.id = :idMedico")
    Boolean findActivoById(@Param("idMedico") Long idMedico);

    // Query para elegir médico aleatorio disponible (con filtro de cancelamiento)
    @Query("""
        SELECT m FROM Medico m
        WHERE m.activo = true
        AND m.especialidad = :especialidad
        AND m.id NOT IN (
            SELECT c.medico.id FROM Consulta c
            WHERE c.fecha = :fecha
            AND c.motivoCancelamiento IS NULL
        )
        ORDER BY RAND()
        LIMIT 1
        """)
    Medico elegirMedicoAleatorioDisponibleEnLaFecha(
            @Param("especialidad") Especialidad especialidad,
            @Param("fecha") LocalDateTime fecha
    );
}
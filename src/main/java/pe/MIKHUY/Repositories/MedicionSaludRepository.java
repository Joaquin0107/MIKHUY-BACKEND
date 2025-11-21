package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.MedicionSalud;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicionSaludRepository extends JpaRepository<MedicionSalud, UUID> {

    /**
     * Obtener todas las mediciones de un estudiante ordenadas por fecha
     */
    List<MedicionSalud> findByEstudianteIdOrderByFechaRegistroDesc(UUID estudianteId);

    /**
     * Obtener la última medición de un estudiante
     */
    Optional<MedicionSalud> findFirstByEstudianteIdOrderByFechaRegistroDesc(UUID estudianteId);

    /**
     * Obtener mediciones de un estudiante en un rango de fechas
     */
    @Query("SELECT m FROM MedicionSalud m WHERE m.estudiante.id = :estudianteId " +
            "AND m.fechaRegistro BETWEEN :fechaInicio AND :fechaFin " +
            "ORDER BY m.fechaRegistro DESC")
    List<MedicionSalud> findByEstudianteIdAndFechaRegistroBetween(
            @Param("estudianteId") UUID estudianteId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtener últimas N mediciones de un estudiante
     */
    @Query("SELECT m FROM MedicionSalud m WHERE m.estudiante.id = :estudianteId " +
            "ORDER BY m.fechaRegistro DESC")
    List<MedicionSalud> findTopNByEstudianteId(@Param("estudianteId") UUID estudianteId);

    /**
     * Contar mediciones de un estudiante
     */
    long countByEstudianteId(UUID estudianteId);
}
package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.AnalisisNutricional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AnalisisNutricionalRepository extends JpaRepository<AnalisisNutricional, UUID> {

    // Buscar por estudiante
    List<AnalisisNutricional> findByEstudianteId(UUID estudianteId);

    // Buscar por estudiante ordenado por fecha descendente
    List<AnalisisNutricional> findByEstudianteIdOrderByFechaAnalisisDesc(UUID estudianteId);

    // Último análisis de un estudiante
    Optional<AnalisisNutricional> findFirstByEstudianteIdOrderByFechaAnalisisDesc(UUID estudianteId);

    // Buscar por estudiante y fecha
    Optional<AnalisisNutricional> findByEstudianteIdAndFechaAnalisis(UUID estudianteId, LocalDate fechaAnalisis);

    // Análisis en un rango de fechas
    List<AnalisisNutricional> findByEstudianteIdAndFechaAnalisisBetween(
            UUID estudianteId,
            LocalDate fechaInicio,
            LocalDate fechaFin
    );

    // Buscar por etapa de cambio
    List<AnalisisNutricional> findByEtapaCambio(String etapaCambio);

    // Estudiantes por etapa de cambio
    @Query("SELECT an.etapaCambio, COUNT(DISTINCT an.estudiante.id) " +
            "FROM AnalisisNutricional an " +
            "WHERE an.fechaAnalisis = CURRENT_DATE " +
            "GROUP BY an.etapaCambio")
    List<Object[]> countEstudiantesPorEtapaCambio();

    // Promedio de porcentaje de aciertos por estudiante
    @Query("SELECT AVG(an.porcentajeAciertos) FROM AnalisisNutricional an " +
            "WHERE an.estudiante.id = :estudianteId " +
            "AND an.porcentajeAciertos IS NOT NULL")
    Double getPromedioAciertosByEstudiante(@Param("estudianteId") UUID estudianteId);

    // Evolución de macronutrientes
    @Query("SELECT an.fechaAnalisis, an.proteinasPorcentaje, " +
            "an.carbohidratosPorcentaje, an.grasasPorcentaje " +
            "FROM AnalisisNutricional an " +
            "WHERE an.estudiante.id = :estudianteId " +
            "ORDER BY an.fechaAnalisis ASC")
    List<Object[]> getEvolucionMacronutrientes(@Param("estudianteId") UUID estudianteId);

    // Contar análisis por estudiante
    long countByEstudianteId(UUID estudianteId);

    // Análisis más recientes (últimos 30 días) - CORREGIDO
    @Query("SELECT an FROM AnalisisNutricional an " +
            "WHERE an.fechaAnalisis >= :fechaLimite " +
            "ORDER BY an.fechaAnalisis DESC")
    List<AnalisisNutricional> findRecentAnalisis(@Param("fechaLimite") LocalDate fechaLimite);
}
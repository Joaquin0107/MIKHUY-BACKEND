package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.CoachRespuesta;

import java.util.List;
import java.util.UUID;

@Repository
public interface CoachRespuestaRepository extends JpaRepository<CoachRespuesta, UUID> {

    // Buscar por sesión
    List<CoachRespuesta> findBySesionId(UUID sesionId);

    // Buscar por sesión ordenado por pregunta
    List<CoachRespuesta> findBySesionIdOrderByPreguntaNumeroAsc(UUID sesionId);

    // Respuestas de un estudiante
    @Query("SELECT cr FROM CoachRespuesta cr WHERE cr.sesion.progreso.estudiante.id = :estudianteId " +
            "ORDER BY cr.fechaRespuesta DESC")
    List<CoachRespuesta> findByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Promedio de respuestas por estudiante
    @Query("SELECT AVG(cr.respuestaValor) FROM CoachRespuesta cr " +
            "WHERE cr.sesion.progreso.estudiante.id = :estudianteId")
    Double getPromedioRespuestasByEstudiante(@Param("estudianteId") UUID estudianteId);

    // Distribución de respuestas por etapa
    @Query("SELECT cr.preguntaEtapa, AVG(cr.respuestaValor) " +
            "FROM CoachRespuesta cr " +
            "WHERE cr.sesion.progreso.estudiante.id = :estudianteId " +
            "GROUP BY cr.preguntaEtapa")
    List<Object[]> getPromediosPorEtapa(@Param("estudianteId") UUID estudianteId);

    // Contar respuestas completas (8 preguntas)
    @Query("SELECT COUNT(DISTINCT cr.sesion.id) FROM CoachRespuesta cr " +
            "WHERE cr.sesion.progreso.estudiante.id = :estudianteId " +
            "GROUP BY cr.sesion.id " +
            "HAVING COUNT(*) = 8")
    long countSesionesCompletas(@Param("estudianteId") UUID estudianteId);

    // Última sesión completa del estudiante
    @Query("SELECT cr FROM CoachRespuesta cr " +
            "WHERE cr.sesion.progreso.estudiante.id = :estudianteId " +
            "AND cr.sesion.id IN (" +
            "  SELECT cr2.sesion.id FROM CoachRespuesta cr2 " +
            "  WHERE cr2.sesion.progreso.estudiante.id = :estudianteId " +
            "  GROUP BY cr2.sesion.id " +
            "  HAVING COUNT(*) = 8" +
            ") " +
            "ORDER BY cr.fechaRespuesta DESC")
    List<CoachRespuesta> findLastCompleteSesion(@Param("estudianteId") UUID estudianteId);
}
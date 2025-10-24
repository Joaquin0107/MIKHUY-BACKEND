package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Reporte;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ReporteRepository extends JpaRepository<Reporte, UUID> {

    // Buscar por estudiante
    List<Reporte> findByEstudianteId(UUID estudianteId);

    // Buscar por estudiante ordenado por fecha descendente
    List<Reporte> findByEstudianteIdOrderByFechaGeneracionDesc(UUID estudianteId);

    // Buscar por profesor
    List<Reporte> findByProfesorId(UUID profesorId);

    // Buscar por tipo de reporte
    List<Reporte> findByTipoReporte(String tipoReporte);

    // Buscar por estudiante y tipo
    List<Reporte> findByEstudianteIdAndTipoReporte(UUID estudianteId, String tipoReporte);

    // Reportes en un rango de fechas
    List<Reporte> findByFechaGeneracionBetween(
            java.time.LocalDateTime fechaInicio,
            java.time.LocalDateTime fechaFin
    );

    // Último reporte de un estudiante
    @Query("SELECT r FROM Reporte r WHERE r.estudiante.id = :estudianteId " +
            "ORDER BY r.fechaGeneracion DESC")
    List<Reporte> findLastByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Reportes generados por un usuario
    List<Reporte> findByGeneradoPorId(UUID generadoPorId);

    // Contar reportes por estudiante
    long countByEstudianteId(UUID estudianteId);

    // Contar reportes por tipo
    @Query("SELECT r.tipoReporte, COUNT(r) FROM Reporte r GROUP BY r.tipoReporte")
    List<Object[]> countByTipoReporte();

    // Reportes del mes actual
    @Query("SELECT r FROM Reporte r WHERE " +
            "EXTRACT(MONTH FROM r.fechaGeneracion) = EXTRACT(MONTH FROM CURRENT_DATE) " +
            "AND EXTRACT(YEAR FROM r.fechaGeneracion) = EXTRACT(YEAR FROM CURRENT_DATE) " +
            "ORDER BY r.fechaGeneracion DESC")
    List<Reporte> findCurrentMonthReports();

    // Reportes de un período específico para un estudiante
    @Query("SELECT r FROM Reporte r WHERE r.estudiante.id = :estudianteId " +
            "AND r.fechaInicio >= :fechaInicio AND r.fechaFin <= :fechaFin")
    List<Reporte> findByEstudianteAndPeriod(
            @Param("estudianteId") UUID estudianteId,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );
}
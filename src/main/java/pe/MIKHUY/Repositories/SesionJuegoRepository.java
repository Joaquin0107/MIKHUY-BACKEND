package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.SesionJuego;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SesionJuegoRepository extends JpaRepository<SesionJuego, UUID> {

    // Buscar por progreso
    List<SesionJuego> findByProgresoId(UUID progresoId);

    // Buscar sesiones completadas
    List<SesionJuego> findByProgresoIdAndCompletadoTrue(UUID progresoId);

    // Buscar por rango de fechas
    List<SesionJuego> findByFechaSesionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Última sesión de un progreso
    @Query("SELECT s FROM SesionJuego s WHERE s.progreso.id = :progresoId ORDER BY s.fechaSesion DESC")
    List<SesionJuego> findLastByProgresoId(@Param("progresoId") UUID progresoId);

    // Total de puntos obtenidos en un progreso
    @Query("SELECT SUM(s.puntosObtenidos) FROM SesionJuego s WHERE s.progreso.id = :progresoId")
    Integer getTotalPuntosByProgreso(@Param("progresoId") UUID progresoId);

    // Sesiones de un estudiante
    @Query("SELECT s FROM SesionJuego s WHERE s.progreso.estudiante.id = :estudianteId ORDER BY s.fechaSesion DESC")
    List<SesionJuego> findByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Contar sesiones por estudiante
    @Query("SELECT COUNT(s) FROM SesionJuego s WHERE s.progreso.estudiante.id = :estudianteId")
    long countByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Tiempo total jugado por estudiante (en segundos)
    @Query("SELECT SUM(s.tiempoJugado) FROM SesionJuego s WHERE s.progreso.estudiante.id = :estudianteId")
    Integer getTiempoTotalByEstudiante(@Param("estudianteId") UUID estudianteId);

    @Query("SELECT SUM(s.puntosObtenidos) FROM SesionJuego s WHERE s.progreso.estudiante.id = :estudianteId")
    Integer getTotalPuntosByEstudiante(@Param("estudianteId") UUID estudianteId);

}
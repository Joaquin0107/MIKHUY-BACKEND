package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.ProgresoJuego;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProgresoJuegoRepository extends JpaRepository<ProgresoJuego, UUID> {

    // Buscar por estudiante
    List<ProgresoJuego> findByEstudianteId(UUID estudianteId);

    // Buscar por estudiante y juego
    Optional<ProgresoJuego> findByEstudianteIdAndJuegoId(UUID estudianteId, UUID juegoId);

    // Buscar por juego
    List<ProgresoJuego> findByJuegoId(UUID juegoId);

    // Buscar juegos completados por estudiante
    List<ProgresoJuego> findByEstudianteIdAndCompletadoTrue(UUID estudianteId);

    // Buscar juegos no completados por estudiante
    List<ProgresoJuego> findByEstudianteIdAndCompletadoFalse(UUID estudianteId);

    // Contar juegos completados por estudiante
    long countByEstudianteIdAndCompletadoTrue(UUID estudianteId);

    // Total de puntos ganados por estudiante
    @Query("SELECT SUM(pg.puntosGanados) FROM ProgresoJuego pg WHERE pg.estudiante.id = :estudianteId")
    Integer getTotalPuntosGanadosByEstudiante(@Param("estudianteId") UUID estudianteId);

    // Progreso de todos los estudiantes en un juego específico
    @Query("SELECT pg FROM ProgresoJuego pg WHERE pg.juego.id = :juegoId ORDER BY pg.puntosGanados DESC")
    List<ProgresoJuego> findRankingByJuego(@Param("juegoId") UUID juegoId);

    // Estudiantes que jugaron recientemente (últimos 7 días) - CORREGIDO
    @Query("SELECT pg FROM ProgresoJuego pg WHERE pg.ultimaJugada >= :fechaLimite")
    List<ProgresoJuego> findRecentlyPlayed(@Param("fechaLimite") LocalDateTime fechaLimite);

    // Promedio de nivel por juego
    @Query("SELECT pg.juego.nombre, AVG(pg.nivelActual) " +
            "FROM ProgresoJuego pg " +
            "GROUP BY pg.juego.nombre")
    List<Object[]> getPromedioNivelPorJuego();

    List<ProgresoJuego> findByJuegoIdOrderByPuntosGanadosDesc(UUID juegoId);
}
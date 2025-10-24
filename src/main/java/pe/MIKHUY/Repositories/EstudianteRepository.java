package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Estudiante;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EstudianteRepository extends JpaRepository<Estudiante, UUID> {

    // Buscar por usuario ID
    Optional<Estudiante> findByUsuarioId(UUID usuarioId);

    // Buscar por grado y sección
    List<Estudiante> findByGradoAndSeccion(String grado, String seccion);

    // Buscar por grado
    List<Estudiante> findByGrado(String grado);

    // Buscar por sección
    List<Estudiante> findBySeccion(String seccion);

    // Top estudiantes por puntos (ranking)
    @Query("SELECT e FROM Estudiante e JOIN e.usuario u WHERE u.activo = true ORDER BY e.puntosAcumulados DESC")
    List<Estudiante> findTopByPuntosAcumuladosOrderByDesc();

    // Estudiantes con más de X puntos
    List<Estudiante> findByPuntosAcumuladosGreaterThanEqual(Integer puntos);

    // Estudiantes por rango de edad
    List<Estudiante> findByEdadBetween(Integer edadMin, Integer edadMax);

    // Buscar estudiantes activos
    @Query("SELECT e FROM Estudiante e WHERE e.usuario.activo = true")
    List<Estudiante> findAllWithActiveUser();

    // Estadísticas por grado (grado, cantidad estudiantes, promedio puntos)
    @Query("SELECT e.grado, COUNT(e), AVG(e.puntosAcumulados) " +
            "FROM Estudiante e " +
            "GROUP BY e.grado " +
            "ORDER BY e.grado")
    List<Object[]> getEstadisticasPorGrado();

    // Estudiantes de un grado con usuario activo
    @Query("SELECT e FROM Estudiante e WHERE e.grado = :grado AND e.usuario.activo = true")
    List<Estudiante> findActiveByGrado(@Param("grado") String grado);

    // Contar estudiantes por grado
    long countByGrado(String grado);

    // Contar estudiantes por grado y sección
    long countByGradoAndSeccion(String grado, String seccion);
}
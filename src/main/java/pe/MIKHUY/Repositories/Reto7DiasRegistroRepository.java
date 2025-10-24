package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Reto7DiasRegistro;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface Reto7DiasRegistroRepository extends JpaRepository<Reto7DiasRegistro, UUID> {

    // Buscar por sesión
    List<Reto7DiasRegistro> findBySesionId(UUID sesionId);

    // Buscar por sesión ordenado por día
    List<Reto7DiasRegistro> findBySesionIdOrderByDiaNumeroAsc(UUID sesionId);

    // Buscar por sesión y día
    List<Reto7DiasRegistro> findBySesionIdAndDiaNumero(UUID sesionId, Integer diaNumero);

    // Buscar por sesión, día y momento
    Optional<Reto7DiasRegistro> findBySesionIdAndDiaNumeroAndMomentoDia(
            UUID sesionId,
            Integer diaNumero,
            Reto7DiasRegistro.MomentoDiaEnum momentoDia
    );

    // Registros de un estudiante
    @Query("SELECT r FROM Reto7DiasRegistro r WHERE r.sesion.progreso.estudiante.id = :estudianteId " +
            "ORDER BY r.fechaRegistro DESC")
    List<Reto7DiasRegistro> findByEstudianteId(@Param("estudianteId") UUID estudianteId);

    // Promedio de calorías por estudiante
    @Query("SELECT AVG(r.caloriasEstimadas) FROM Reto7DiasRegistro r " +
            "WHERE r.sesion.progreso.estudiante.id = :estudianteId " +
            "AND r.caloriasEstimadas IS NOT NULL")
    Double getPromedioCaloriasByEstudiante(@Param("estudianteId") UUID estudianteId);

    // Distribución de alimentos por grupo
    @Query("SELECT " +
            "SUM(r.alimentosFrutas), " +
            "SUM(r.alimentosVerduras), " +
            "SUM(r.alimentosProteinas), " +
            "SUM(r.alimentosCarbohidratos), " +
            "SUM(r.alimentosLacteos), " +
            "SUM(r.alimentosDulces) " +
            "FROM Reto7DiasRegistro r " +
            "WHERE r.sesion.progreso.estudiante.id = :estudianteId")
    List<Object[]> getDistribucionAlimentos(@Param("estudianteId") UUID estudianteId);

    // Emociones más frecuentes por estudiante
    @Query("SELECT r.emocion, COUNT(*) FROM Reto7DiasRegistro r " +
            "WHERE r.sesion.progreso.estudiante.id = :estudianteId " +
            "AND r.emocion IS NOT NULL " +
            "GROUP BY r.emocion " +
            "ORDER BY COUNT(*) DESC")
    List<Object[]> getEmocionesFrecuentes(@Param("estudianteId") UUID estudianteId);

    // Verificar si completó los 7 días
    @Query("SELECT COUNT(DISTINCT r.diaNumero) FROM Reto7DiasRegistro r " +
            "WHERE r.sesion.id = :sesionId")
    long countDiasCompletados(@Param("sesionId") UUID sesionId);
}
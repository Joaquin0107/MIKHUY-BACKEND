package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Canje;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CanjeRepository extends JpaRepository<Canje, UUID> {

    // Buscar por estudiante
    List<Canje> findByEstudianteId(UUID estudianteId);

    // Buscar por estudiante ordenado por fecha descendente
    List<Canje> findByEstudianteIdOrderByFechaCanjeDesc(UUID estudianteId);

    // Buscar por estado
    List<Canje> findByEstado(Canje.EstadoCanjeEnum estado);

    // Buscar por estudiante y estado
    List<Canje> findByEstudianteIdAndEstado(UUID estudianteId, Canje.EstadoCanjeEnum estado);

    // Canjes pendientes de un estudiante
    List<Canje> findByEstudianteIdAndEstadoOrderByFechaCanjeDesc(
            UUID estudianteId,
            Canje.EstadoCanjeEnum estado
    );

    // Canjes en un rango de fechas
    List<Canje> findByFechaCanjeBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Total de puntos gastados por estudiante
    @Query("SELECT SUM(c.puntosGastados) FROM Canje c " +
            "WHERE c.estudiante.id = :estudianteId")
    Integer getTotalPuntosGastadosByEstudiante(@Param("estudianteId") UUID estudianteId);

    // Contar canjes por estudiante
    long countByEstudianteId(UUID estudianteId);

    // Contar canjes por beneficio
    long countByBeneficioId(UUID beneficioId);

    // Beneficios más canjeados
    @Query("SELECT c.beneficio.nombre, COUNT(c), SUM(c.cantidad) " +
            "FROM Canje c " +
            "GROUP BY c.beneficio.nombre " +
            "ORDER BY COUNT(c) DESC")
    List<Object[]> getBeneficiosMasCanjeados();

    // Canjes por estado (estadísticas)
    @Query("SELECT c.estado, COUNT(c) " +
            "FROM Canje c " +
            "GROUP BY c.estado")
    List<Object[]> getEstadisticasPorEstado();

    // Últimos canjes globales
    List<Canje> findTop10ByOrderByFechaCanjeDesc();

    // Canjes de un beneficio específico
    List<Canje> findByBeneficioIdOrderByFechaCanjeDesc(UUID beneficioId);
}
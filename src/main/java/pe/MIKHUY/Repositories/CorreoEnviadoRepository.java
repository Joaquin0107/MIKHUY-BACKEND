package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.CorreoEnviado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CorreoEnviadoRepository extends JpaRepository<CorreoEnviado, UUID> {

    // Buscar por reporte
    List<CorreoEnviado> findByReporteId(UUID reporteId);

    // Buscar por estado
    List<CorreoEnviado> findByEstado(CorreoEnviado.EstadoCorreoEnum estado);

    // Buscar por destinatario
    List<CorreoEnviado> findByDestinatarioEmail(String destinatarioEmail);

    // Correos en un rango de fechas
    List<CorreoEnviado> findByFechaEnvioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    // Correos fallidos
    List<CorreoEnviado> findByEstadoOrderByFechaEnvioDesc(CorreoEnviado.EstadoCorreoEnum estado);

    // Contar correos por estado
    @Query("SELECT c.estado, COUNT(c) FROM CorreoEnviado c GROUP BY c.estado")
    List<Object[]> countByEstado();

    // Últimos correos enviados
    List<CorreoEnviado> findTop20ByOrderByFechaEnvioDesc();

    // Correos pendientes
    List<CorreoEnviado> findByEstadoOrderByFechaEnvioAsc(CorreoEnviado.EstadoCorreoEnum estado);
}
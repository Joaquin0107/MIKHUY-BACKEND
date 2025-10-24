package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Notificacion;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    // Buscar por usuario
    List<Notificacion> findByUsuarioId(UUID usuarioId);

    // Buscar por usuario ordenado por fecha descendente
    List<Notificacion> findByUsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);

    // Notificaciones no leídas de un usuario
    List<Notificacion> findByUsuarioIdAndLeidaFalse(UUID usuarioId);

    // Notificaciones no leídas ordenadas por fecha
    List<Notificacion> findByUsuarioIdAndLeidaFalseOrderByFechaCreacionDesc(UUID usuarioId);

    // Contar notificaciones no leídas
    long countByUsuarioIdAndLeidaFalse(UUID usuarioId);

    // Buscar por tipo
    List<Notificacion> findByTipo(String tipo);

    // Buscar por usuario y tipo
    List<Notificacion> findByUsuarioIdAndTipo(UUID usuarioId, String tipo);

    // Marcar todas como leídas para un usuario
    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true WHERE n.usuario.id = :usuarioId AND n.leida = false")
    int marcarTodasComoLeidas(@Param("usuarioId") UUID usuarioId);

    // Últimas notificaciones de un usuario (limitadas)
    List<Notificacion> findTop10ByUsuarioIdOrderByFechaCreacionDesc(UUID usuarioId);

    // Eliminar notificaciones antiguas (más de X días)
    @Modifying
    @Query("DELETE FROM Notificacion n WHERE n.fechaCreacion < :fecha")
    int deleteOlderThan(@Param("fecha") java.time.LocalDateTime fecha);
}
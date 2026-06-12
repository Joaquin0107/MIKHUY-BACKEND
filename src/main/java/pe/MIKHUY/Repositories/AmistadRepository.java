package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.MIKHUY.Entities.Amistad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AmistadRepository extends JpaRepository<Amistad, UUID> {

    @Query("SELECT a FROM Amistad a WHERE " +
            "(a.solicitanteId = :a AND a.receptorId = :b) OR " +
            "(a.solicitanteId = :b AND a.receptorId = :a)")
    Optional<Amistad> findEntre(@Param("a") UUID a, @Param("b") UUID b);

    @Query("SELECT COUNT(a) > 0 FROM Amistad a WHERE a.estado = 'aceptada' AND " +
            "((a.solicitanteId = :a AND a.receptorId = :b) OR " +
            "(a.solicitanteId = :b AND a.receptorId = :a))")
    boolean sonAmigos(@Param("a") UUID a, @Param("b") UUID b);

    @Query("SELECT CASE WHEN a.solicitanteId = :miId THEN a.receptorId ELSE a.solicitanteId END " +
            "FROM Amistad a WHERE a.estado = 'aceptada' AND (a.solicitanteId = :miId OR a.receptorId = :miId)")
    List<UUID> findAmigosIds(@Param("miId") UUID miId);

    @Query("SELECT a FROM Amistad a WHERE a.receptorId = :miId AND a.estado = 'pendiente'")
    List<Amistad> findSolicitudesRecibidas(@Param("miId") UUID miId);

    @Query("SELECT a FROM Amistad a WHERE a.solicitanteId = :miId AND a.estado = 'pendiente'")
    List<Amistad> findSolicitudesEnviadas(@Param("miId") UUID miId);

    @Modifying
    @Query("DELETE FROM Amistad a WHERE " +
            "(a.solicitanteId = :a AND a.receptorId = :b) OR " +
            "(a.solicitanteId = :b AND a.receptorId = :a)")
    void eliminar(@Param("a") UUID a, @Param("b") UUID b);
}
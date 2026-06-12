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
            "(a.estudiante1Id = :a AND a.estudiante2Id = :b) OR " +
            "(a.estudiante1Id = :b AND a.estudiante2Id = :a)")
    Optional<Amistad> findAmistad(@Param("a") UUID a, @Param("b") UUID b);

    @Query("SELECT COUNT(a) > 0 FROM Amistad a WHERE " +
            "(a.estudiante1Id = :a AND a.estudiante2Id = :b) OR " +
            "(a.estudiante1Id = :b AND a.estudiante2Id = :a)")
    boolean existeAmistad(@Param("a") UUID a, @Param("b") UUID b);

    @Query("SELECT CASE WHEN a.estudiante1Id = :miId THEN a.estudiante2Id ELSE a.estudiante1Id END " +
            "FROM Amistad a WHERE a.estudiante1Id = :miId OR a.estudiante2Id = :miId")
    List<UUID> findAmigosIds(@Param("miId") UUID miId);

    @Modifying
    @Query("DELETE FROM Amistad a WHERE " +
            "(a.estudiante1Id = :a AND a.estudiante2Id = :b) OR " +
            "(a.estudiante1Id = :b AND a.estudiante2Id = :a)")
    void eliminarAmistad(@Param("a") UUID a, @Param("b") UUID b);
}
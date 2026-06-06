package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Usuario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<Usuario> findByEmailAndActivoTrue(String email);
    List<Usuario> findByRolAndActivoTrue(Usuario.RolEnum rol);
    List<Usuario> findByActivoTrue();

    // ── Verificación de cuenta ──────────────────────────────────────────────
    Optional<Usuario> findByTokenVerificacion(String token);

    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Usuario> searchByNombreOrApellido(@Param("search") String search);

    long countByRol(Usuario.RolEnum rol);

    @Query("SELECT u FROM Usuario u WHERE u.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Usuario> findByFechaCreacionBetween(
            @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
            @Param("fechaFin") java.time.LocalDateTime fechaFin);
}
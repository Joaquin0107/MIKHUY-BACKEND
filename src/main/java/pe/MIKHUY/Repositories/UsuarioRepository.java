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

    // Buscar por email
    Optional<Usuario> findByEmail(String email);

    // Verificar si existe por email
    boolean existsByEmail(String email);

    // Buscar por email y activo
    Optional<Usuario> findByEmailAndActivoTrue(String email);

    // Buscar usuarios activos por rol
    List<Usuario> findByRolAndActivoTrue(Usuario.RolEnum rol);

    // Buscar todos los usuarios activos
    List<Usuario> findByActivoTrue();

    // Buscar usuarios por nombre o apellido (búsqueda parcial)
    @Query("SELECT u FROM Usuario u WHERE " +
            "LOWER(u.nombres) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Usuario> searchByNombreOrApellido(@Param("search") String search);

    // Contar usuarios por rol
    long countByRol(Usuario.RolEnum rol);

    // Usuarios registrados en un rango de fechas
    @Query("SELECT u FROM Usuario u WHERE u.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Usuario> findByFechaCreacionBetween(@Param("fechaInicio") java.time.LocalDateTime fechaInicio,
                                             @Param("fechaFin") java.time.LocalDateTime fechaFin);
}
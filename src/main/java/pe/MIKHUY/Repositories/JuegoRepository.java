package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Juego;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JuegoRepository extends JpaRepository<Juego, UUID> {

    // Buscar juegos activos
    List<Juego> findByActivoTrue();

    // Buscar por nombre
    Optional<Juego> findByNombre(String nombre);

    // Buscar por nombre (case insensitive)
    Optional<Juego> findByNombreIgnoreCase(String nombre);

    // Buscar por categoría
    List<Juego> findByCategoria(String categoria);

    // Buscar por categoría y activos
    List<Juego> findByCategoriaAndActivoTrue(String categoria);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}
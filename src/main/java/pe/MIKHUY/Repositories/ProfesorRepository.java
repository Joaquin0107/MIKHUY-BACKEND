package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Profesor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfesorRepository extends JpaRepository<Profesor, UUID> {

    // Buscar por usuario ID
    Optional<Profesor> findByUsuarioId(UUID usuarioId);

    // Buscar por materia
    List<Profesor> findByMateria(String materia);

    // Buscar profesores activos
    @Query("SELECT p FROM Profesor p WHERE p.usuario.activo = true")
    List<Profesor> findAllWithActiveUser();

    // Contar profesores por materia
    long countByMateria(String materia);
}
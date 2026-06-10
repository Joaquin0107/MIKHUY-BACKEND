package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.ResultadoMicronutrientes;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResultadoMicronutrientesRepository extends JpaRepository<ResultadoMicronutrientes, UUID> {
    List<ResultadoMicronutrientes> findBySesionId(UUID sesionId);

    @Query("SELECT r FROM ResultadoMicronutrientes r WHERE r.sesion.progreso.estudiante.id = :estudianteId ORDER BY r.nivelNumero ASC")
    List<ResultadoMicronutrientes> findBySesionEstudianteId(@Param("estudianteId") UUID estudianteId);

}
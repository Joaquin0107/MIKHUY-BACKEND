package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.GrupoEstudio;
import java.util.List;
import java.util.UUID;

@Repository
public interface GrupoEstudioRepository extends JpaRepository<GrupoEstudio, UUID> {

    @Query("SELECT g FROM GrupoEstudio g WHERE g.profesor.usuario.id = :usuarioId")
    List<GrupoEstudio> findByProfesorUsuarioId(@Param("usuarioId") UUID usuarioId);
}
package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.ResultadoClasifica;
import java.util.List;
import java.util.UUID;

@Repository
public interface ResultadoClasificaRepository extends JpaRepository<ResultadoClasifica, UUID> {
    List<ResultadoClasifica> findBySesionId(UUID sesionId);
}
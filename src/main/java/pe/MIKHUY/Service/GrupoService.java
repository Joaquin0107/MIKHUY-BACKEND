package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.request.CrearGrupoRequest;
import pe.MIKHUY.DTOs.response.GrupoResumenResponse;
import java.util.List;
import java.util.UUID;

public interface GrupoService {
    GrupoResumenResponse crearGrupo(UUID profesorUsuarioId, CrearGrupoRequest request);
    List<GrupoResumenResponse> getGruposDelProfesor(UUID profesorUsuarioId);
    GrupoResumenResponse getGrupoDetalle(UUID grupoId);
    GrupoResumenResponse actualizarGrupo(UUID grupoId, CrearGrupoRequest request);
    void eliminarGrupo(UUID grupoId);
}
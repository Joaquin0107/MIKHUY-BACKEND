package pe.MIKHUY.DTOs.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class GrupoResumenResponse {
    private UUID id;
    private String nombre;
    private int totalMiembros;
    private String fechaCreacion;
    private List<MiembroGrupoResponse> miembros;
    // Métricas compiladas
    private int promedioPuntos;
    private int totalSesionesGrupo;
    private String alumnoMasActivo;
    private String juegoMasDominado;
    private List<ComparativaJuegoResponse> comparativaJuegos;
}
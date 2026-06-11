package pe.MIKHUY.DTOs.request;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class CrearGrupoRequest {
    private String nombre;
    private List<UUID> estudianteIds; // máx 5
}
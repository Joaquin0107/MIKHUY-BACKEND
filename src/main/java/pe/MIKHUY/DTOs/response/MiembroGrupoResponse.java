package pe.MIKHUY.DTOs.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class MiembroGrupoResponse {
    private UUID id;
    private String nombre;
    private String apellido;
    private int puntosAcumulados;
    private int totalSesiones;
    private int juegosCompletados;
    private int posicionEnGrupo; // ranking interno
}
package pe.MIKHUY.DTOs.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ComparativaJuegoResponse {
    private String juegoNombre;
    private double promedioProgreso; // % promedio del grupo
    private int totalCompletados;    // cuántos del grupo lo completaron
}
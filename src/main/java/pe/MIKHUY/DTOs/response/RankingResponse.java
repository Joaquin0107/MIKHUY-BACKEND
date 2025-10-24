package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RankingResponse {

    private List<EstudianteRanking> ranking;
    private Integer totalEstudiantes;

    // Información del estudiante actual (si está autenticado)
    private EstudianteRanking miPosicion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class EstudianteRanking {
        private Integer posicion;
        private UUID estudianteId;
        private String nombre;
        private String grado;
        private String seccion;
        private Integer puntosAcumulados;
        private String avatarUrl;

        // Indicadores visuales
        private Boolean esTop3;
        private Boolean esMiPosicion;

        // Progreso
        private Integer juegosCompletados;
    }
}
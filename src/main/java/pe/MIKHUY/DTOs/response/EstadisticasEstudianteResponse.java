package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadisticasEstudianteResponse {

    // Puntos
    private Integer puntosAcumulados;
    private Integer puntosGanados; // total de puntos ganados en juegos
    private Integer puntosGastados; // total de puntos gastados en canjes

    // Juegos
    private Integer juegosJugados;
    private Integer juegosCompletados;
    private Integer totalSesiones;
    private Integer tiempoTotalJugado; // en segundos

    // Ranking
    private Integer posicionRanking;
    private Integer totalEstudiantes;

    // Progreso por juego
    private List<ProgresoJuegoResumen> progresoJuegos;

    // Análisis nutricional
    private Boolean tieneAnalisisReciente;
    private String etapaCambioActual;
    private Double porcentajeAciertosPromedio;

    // Canjes
    private Integer totalCanjes;
    private Integer canjesPendientes;

    // Notificaciones
    private Integer notificacionesNoLeidas;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProgresoJuegoResumen {
        private String juegoNombre;
        private Integer nivelActual;
        private Integer puntosGanados;
        private Boolean completado;
        private Double porcentajeCompletado;
    }
}
package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProgresoJuegoResponse {

    private UUID id;

    // Datos del juego
    private UUID juegoId;
    private String juegoNombre;
    private String juegoDescripcion;
    private String juegoCategoria;
    private Integer juegoMaxNiveles;
    private Integer juegoPuntosPorNivel;

    // Progreso
    private Integer nivelActual;
    private Integer puntosGanados;
    private Integer vecesJugado;
    private LocalDateTime ultimaJugada;
    private Boolean completado;
    private Double porcentajeCompletado;

    // Fechas
    private LocalDateTime fechaInicio;
}
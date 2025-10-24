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
public class SesionJuegoResponse {

    private UUID id;
    private UUID progresoId;

    // Datos del juego
    private String juegoNombre;
    private String juegoCategoria;

    // Datos de la sesión
    private Integer nivelJugado;
    private Integer puntosObtenidos;
    private Integer tiempoJugado; // en segundos
    private String tiempoJugadoFormato; // formato HH:MM:SS
    private Boolean completado;
    private LocalDateTime fechaSesion;
}
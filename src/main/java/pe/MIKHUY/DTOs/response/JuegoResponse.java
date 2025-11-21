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
public class JuegoResponse {

    private UUID id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private Integer maxNiveles;
    private Integer puntosPorNivel;
    private Integer puntosMaximos;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private String imagen;

    // Datos de progreso del estudiante (opcional)
    private UUID progresoId;
    private Integer nivelActual;
    private Integer puntosGanados;
    private Integer vecesJugado;
    private LocalDateTime ultimaJugada;
    private Boolean completado;
    private Double porcentajeCompletado;
}
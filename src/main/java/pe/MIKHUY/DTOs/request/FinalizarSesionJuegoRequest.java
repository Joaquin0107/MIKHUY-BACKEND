package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarSesionJuegoRequest {

    @NotNull(message = "El ID de la sesión es obligatorio")
    private UUID sesionId;

    @NotNull(message = "Los puntos obtenidos son obligatorios")
    @Min(value = 0, message = "Los puntos no pueden ser negativos")
    private Integer puntosObtenidos;

    @Min(value = 0, message = "El tiempo no puede ser negativo")
    private Integer tiempoJugado; // en segundos

    @NotNull(message = "El estado de completitud es obligatorio")
    private Boolean completado;
}
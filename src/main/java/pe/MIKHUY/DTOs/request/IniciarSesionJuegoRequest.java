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
public class IniciarSesionJuegoRequest {

    @NotNull(message = "El ID del juego es obligatorio")
    private UUID juegoId;

    @NotNull(message = "El nivel es obligatorio")
    @Min(value = 1, message = "El nivel debe ser mayor a 0")
    private Integer nivel;
}
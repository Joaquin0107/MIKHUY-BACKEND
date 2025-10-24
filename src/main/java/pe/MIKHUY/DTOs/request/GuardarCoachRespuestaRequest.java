package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardarCoachRespuestaRequest {

    @NotNull(message = "El ID de la sesión es obligatorio")
    private UUID sesionId;

    @NotNull(message = "El número de pregunta es obligatorio")
    @Min(value = 1, message = "El número de pregunta debe estar entre 1 y 8")
    @Max(value = 8, message = "El número de pregunta debe estar entre 1 y 8")
    private Integer preguntaNumero;

    private String preguntaEtapa; // Pre-contemplación, Contemplación, etc.

    @NotNull(message = "El valor de respuesta es obligatorio")
    @Min(value = 1, message = "El valor debe estar entre 1 y 5")
    @Max(value = 5, message = "El valor debe estar entre 1 y 5")
    private Integer respuestaValor;
}
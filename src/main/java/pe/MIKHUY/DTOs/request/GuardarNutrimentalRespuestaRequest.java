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
public class GuardarNutrimentalRespuestaRequest {

    @NotNull(message = "El ID de la sesión es obligatorio")
    private UUID sesionId;

    @NotNull(message = "El número de pregunta es obligatorio")
    @Min(value = 1, message = "El número de pregunta debe ser mayor a 0")
    private Integer preguntaNumero;

    private String preguntaTema;

    @NotNull(message = "La respuesta correcta es obligatoria")
    private Boolean respuestaCorrecta;

    @Min(value = 0, message = "El tiempo de respuesta no puede ser negativo")
    private Integer tiempoRespuesta; // en segundos
}
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
public class GuardarReto7DiasRegistroRequest {

    @NotNull(message = "El ID de la sesión es obligatorio")
    private UUID sesionId;

    @NotNull(message = "El día es obligatorio")
    @Min(value = 1, message = "El día debe estar entre 1 y 7")
    @Max(value = 7, message = "El día debe estar entre 1 y 7")
    private Integer diaNumero;

    @NotNull(message = "El momento del día es obligatorio")
    private String momentoDia; // Desayuno, Almuerzo, Cena

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosFrutas;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosVerduras;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosProteinas;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosCarbohidratos;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosLacteos;

    @Min(value = 0, message = "La cantidad no puede ser negativa")
    private Integer alimentosDulces;

    private String emocion; // feliz, normal, triste, estresado, ansioso

    @Min(value = 0, message = "Las calorías no pueden ser negativas")
    private Integer caloriasEstimadas;

    private String notas;
}
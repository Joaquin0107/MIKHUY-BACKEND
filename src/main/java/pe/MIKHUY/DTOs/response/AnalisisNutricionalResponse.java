package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnalisisNutricionalResponse {

    private UUID id;
    private UUID estudianteId;
    private String estudianteNombre;
    private LocalDate fechaAnalisis;

    // Macronutrientes
    private BigDecimal proteinasPorcentaje;
    private BigDecimal carbohidratosPorcentaje;
    private BigDecimal grasasPorcentaje;

    // Vitaminas y minerales
    private BigDecimal vitaminaA;
    private BigDecimal vitaminaC;
    private BigDecimal calcio;
    private BigDecimal hierro;

    // Evaluación balance nutricional
    private String evaluacionMacronutrientes; // Balanceado, Desequilibrado
    private String evaluacionMicronutrientes; // Adecuado, Deficiente

    // Datos psicológicos
    private String etapaCambio;
    private BigDecimal puntajeMotivacion;
    private Integer disposicionCambio;

    // Conocimiento nutricional
    private BigDecimal porcentajeAciertos;
    private List<String> temasDebiles;

    // Recomendaciones
    private List<String> recomendaciones;

    private LocalDateTime fechaCreacion;
}
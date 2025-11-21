package pe.MIKHUY.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicionSaludResponse {
    private String id;
    private String estudianteId;
    private BigDecimal peso;
    private BigDecimal talla;
    private BigDecimal imc;
    private String estadoNutricional;
    private LocalDateTime fechaRegistro;
    private String notas;
}
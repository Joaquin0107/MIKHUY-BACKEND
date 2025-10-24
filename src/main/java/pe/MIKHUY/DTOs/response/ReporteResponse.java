package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReporteResponse {

    private UUID id;

    // Datos del estudiante
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteGrado;
    private String estudianteSeccion;

    // Datos del profesor (opcional)
    private UUID profesorId;
    private String profesorNombre;

    // Tipo y período
    private String tipoReporte; // mensual, trimestral, personalizado
    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    // Contenido del reporte
    private Map<String, Object> contenido;

    // PDF
    private String pdfUrl;
    private Boolean pdfDisponible;

    // Generación
    private String generadoPorNombre;
    private LocalDateTime fechaGeneracion;
}
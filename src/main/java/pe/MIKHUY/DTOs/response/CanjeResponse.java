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
public class CanjeResponse {

    private UUID id;

    // Datos del estudiante
    private UUID estudianteId;
    private String estudianteNombre;
    private String estudianteGrado;
    private String estudianteSeccion;

    // Datos del beneficio
    private UUID beneficioId;
    private String beneficioNombre;
    private String beneficioDescripcion;
    private String beneficioCategoria;
    private String beneficioImagenUrl;
    private Integer beneficioPuntosRequeridos;

    // Datos del canje
    private Integer cantidad;
    private Integer puntosGastados;
    private String estado; // pendiente, entregado, cancelado

    // Fechas
    private LocalDateTime fechaCanje;
    private LocalDateTime fechaEntrega;

    // Información adicional
    private Boolean puedeSerCancelado;
    private String tiempoTranscurrido; // "Hace 2 horas"
}
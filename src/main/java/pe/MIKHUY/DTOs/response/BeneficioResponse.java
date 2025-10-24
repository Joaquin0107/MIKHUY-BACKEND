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
public class BeneficioResponse {

    private UUID id;
    private String nombre;
    private String descripcion;
    private Integer puntosRequeridos;
    private String categoria;
    private Integer stock;
    private Boolean disponible; // stock > 0
    private String imagenUrl;
    private Boolean activo;
    private LocalDateTime fechaCreacion;

    // Estadísticas
    private Integer vecesCanjeado;
    private Boolean puedeCanjearse; // si el estudiante tiene suficientes puntos
}
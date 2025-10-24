package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstudianteResponse {

    private UUID id;
    private UUID usuarioId;

    // Datos del usuario
    private String email;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String telefono;
    private String avatarUrl;

    // Datos del estudiante
    private Integer edad;
    private String grado;
    private String seccion;
    private BigDecimal talla;
    private BigDecimal peso;
    private BigDecimal imc;
    private Integer puntosAcumulados;

    // Estadísticas
    private Integer juegosJugados;
    private Integer juegosCompletados;
    private Integer totalSesiones;

    // Fechas
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimaConexion;
}
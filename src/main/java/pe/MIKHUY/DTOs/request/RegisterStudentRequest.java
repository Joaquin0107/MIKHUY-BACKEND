package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterStudentRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefono;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 6, message = "La edad mínima es 6 años")
    @Max(value = 18, message = "La edad máxima es 18 años")
    private Integer edad;

    @NotBlank(message = "El grado es obligatorio")
    @Size(max = 10, message = "El grado no puede exceder 10 caracteres")
    private String grado;

    @NotBlank(message = "La sección es obligatoria")
    @Size(max = 5, message = "La sección no puede exceder 5 caracteres")
    private String seccion;

    @DecimalMin(value = "0.5", message = "La talla debe ser mayor a 0.5 metros")
    @DecimalMax(value = "3.0", message = "La talla debe ser menor a 3 metros")
    private BigDecimal talla;

    @DecimalMin(value = "10.0", message = "El peso debe ser mayor a 10 kg")
    @DecimalMax(value = "200.0", message = "El peso debe ser menor a 200 kg")
    private BigDecimal peso;
}
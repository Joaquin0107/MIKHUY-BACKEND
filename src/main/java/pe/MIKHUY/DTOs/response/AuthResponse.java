package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private UserInfo user;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserInfo {
        private UUID id;
        private String email;
        private String rol;
        private String nombres;
        private String apellidos;
        private String nombreCompleto;
        private String avatarUrl;

        // Campos específicos de estudiante
        private UUID estudianteId;
        private Integer puntosAcumulados;
        private String grado;
        private String seccion;

        // Campos específicos de profesor
        private UUID profesorId;
        private String materia;
    }
}
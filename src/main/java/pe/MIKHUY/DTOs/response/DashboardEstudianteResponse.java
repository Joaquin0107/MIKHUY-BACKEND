package pe.MIKHUY.DTOs.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardEstudianteResponse {

    // Información del estudiante
    private EstudianteResponse estudiante;

    // Estadísticas generales
    private EstadisticasEstudianteResponse estadisticas;

    // Juegos disponibles con progreso
    private List<JuegoResponse> juegos;

    // Últimas sesiones jugadas
    private List<SesionJuegoResponse> ultimasSesiones;

    // Notificaciones recientes
    private List<NotificacionResponse> notificaciones;

    // Beneficios disponibles
    private List<BeneficioResponse> beneficiosDisponibles;

    // Último análisis nutricional
    private AnalisisNutricionalResponse ultimoAnalisis;

    // Ranking
    private RankingInfo ranking;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingInfo {
        private Integer posicion;
        private Integer total;
        private List<TopEstudiante> top5;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopEstudiante {
        private String nombre;
        private Integer puntos;
        private Integer posicion;
    }
}
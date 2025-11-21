package pe.MIKHUY.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardEstudianteResponse {
    private EstudianteResponse estudiante;
    private EstadisticasEstudianteResponse estadisticas;
    private List<JuegoResponse> juegos;
    private List<SesionJuegoResponse> ultimasSesiones;
    private List<NotificacionResponse> notificaciones;
    private List<BeneficioResponse> beneficiosDisponibles;
    private AnalisisNutricionalResponse ultimoAnalisis;
    private RankingInfo ranking;

    // ✅ NUEVO: Información de salud
    private SaludInfo salud;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RankingInfo {
        private Integer posicion;
        private Integer total;
        private List<TopEstudiante> top5;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TopEstudiante {
        private String nombre;
        private Integer puntos;
        private Integer posicion;
    }

    /**
     * ✅ NUEVO: Información consolidada de salud del estudiante
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SaludInfo {
        private MedicionSaludResponse medicionActual; // Última medición
        private List<MedicionSaludResponse> historialMediciones; // Últimas 12 mediciones
        private EstadisticasSalud estadisticas;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class EstadisticasSalud {
        private Double imcActual;
        private String estadoNutricionalActual;
        private Double variacionPeso; // Cambio de peso vs medición anterior (%)
        private Double variacionTalla; // Cambio de talla vs medición anterior (%)
        private Integer totalMediciones;
        private String tendencia; // "Mejorando", "Estable", "Preocupante"
        private String recomendacion; // Mensaje personalizado según estado
    }
}
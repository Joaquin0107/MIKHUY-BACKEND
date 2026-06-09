package pe.MIKHUY.DTOs.request;

import lombok.Data;
import java.util.List;

@Data
public class GuardarMicronutrientesRequest {
        private String sesionId;
        private Integer nivelNumero;
        private String pregunta;
        private List<String> deficientesCorrectos;
        private List<String> deficientesSeleccionados;
        private Integer aciertos;
        private Integer puntosObtenidos;
        private Boolean tiempoAgotado;
}
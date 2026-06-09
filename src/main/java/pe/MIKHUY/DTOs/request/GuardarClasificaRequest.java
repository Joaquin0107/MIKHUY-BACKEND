package pe.MIKHUY.DTOs.request;

import lombok.Data;
import java.util.List;

@Data
public class GuardarClasificaRequest {
        private String sesionId;
        private Integer nivelNumero;
        private String grupoObjetivo;
        private List<String> alimentosCorrectos;
        private List<String> alimentosSeleccionados;
        private Integer aciertos;
        private Integer puntosObtenidos;
        private Boolean tiempoAgotado;
        private Integer tiempoUsado;
}
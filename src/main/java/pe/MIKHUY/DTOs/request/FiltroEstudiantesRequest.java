package pe.MIKHUY.DTOs.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FiltroEstudiantesRequest {

    private String grado;
    private String seccion;
    private Integer edadMin;
    private Integer edadMax;
    private Integer puntosMin;
    private Integer puntosMax;
    private String ordenarPor; // puntos, nombre, grado, fecha
    private String direccion; // asc, desc
    private Integer pagina = 0;
    private Integer tamanoPagina = 10;
}
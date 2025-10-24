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
public class PaginatedResponse<T> {

    private List<T> contenido;
    private Integer paginaActual;
    private Integer tamanoPagina;
    private Long totalElementos;
    private Integer totalPaginas;
    private Boolean esUltimaPagina;
    private Boolean esPrimeraPagina;
}
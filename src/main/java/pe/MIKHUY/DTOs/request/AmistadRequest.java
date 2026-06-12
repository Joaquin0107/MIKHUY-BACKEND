package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class AmistadRequest {
    @NotNull
    private UUID otroEstudianteId;
}
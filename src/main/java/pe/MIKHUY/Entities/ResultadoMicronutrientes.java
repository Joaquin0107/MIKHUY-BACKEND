package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resultado_micronutrientes")
@Data
@NoArgsConstructor
public class ResultadoMicronutrientes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJuego sesion;

    @Column(name = "nivel_numero")
    private Integer nivelNumero;

    @Column(name = "pregunta", columnDefinition = "TEXT")
    private String pregunta;

    @Column(name = "deficientes_correctos", columnDefinition = "TEXT[]")
    private List<String> deficientesCorrectos;

    @Column(name = "deficientes_seleccionados", columnDefinition = "TEXT[]")
    private List<String> deficientesSeleccionados;

    @Column(name = "aciertos")
    private Integer aciertos = 0;

    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos = 0;

    @Column(name = "tiempo_agotado")
    private Boolean tiempoAgotado = false;

    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();
}
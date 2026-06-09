package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "resultado_clasifica")
@Data
@NoArgsConstructor
public class ResultadoClasifica {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJuego sesion;

    @Column(name = "nivel_numero")
    private Integer nivelNumero;

    @Column(name = "grupo_objetivo", length = 100)
    private String grupoObjetivo;

    @Column(name = "alimentos_correctos", columnDefinition = "TEXT[]")
    private List<String> alimentosCorrectos;

    @Column(name = "alimentos_seleccionados", columnDefinition = "TEXT[]")
    private List<String> alimentosSeleccionados;

    @Column(name = "aciertos")
    private Integer aciertos = 0;

    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos = 0;

    @Column(name = "tiempo_agotado")
    private Boolean tiempoAgotado = false;

    @Column(name = "tiempo_usado")
    private Integer tiempoUsado = 0;

    @Column(name = "fecha")
    private LocalDateTime fecha = LocalDateTime.now();
}
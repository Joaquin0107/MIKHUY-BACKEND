package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coach_respuestas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CoachRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJuego sesion;

    @Column(name = "pregunta_numero", nullable = false)
    private Integer preguntaNumero; // 1-8

    @Column(name = "pregunta_etapa", length = 50)
    private String preguntaEtapa; // ej: 'Pre-contemplación', 'Contemplación'

    @Column(name = "respuesta_valor", nullable = false)
    private Integer respuestaValor; // 1-5

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @PrePersist
    protected void onCreate() {
        if (fechaRespuesta == null) {
            fechaRespuesta = LocalDateTime.now();
        }
    }
}
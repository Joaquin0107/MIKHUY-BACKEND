package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "nutrimental_respuestas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NutrimentalRespuesta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJuego sesion;

    @Column(name = "pregunta_numero", nullable = false)
    private Integer preguntaNumero;

    @Column(name = "pregunta_tema", length = 100)
    private String preguntaTema; // ej: 'Vitaminas', 'Grupos Alimenticios'

    @Column(name = "respuesta_correcta", nullable = false)
    private Boolean respuestaCorrecta;

    @Column(name = "tiempo_respuesta")
    private Integer tiempoRespuesta; // en segundos

    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;

    @PrePersist
    protected void onCreate() {
        if (fechaRespuesta == null) {
            fechaRespuesta = LocalDateTime.now();
        }
    }
}
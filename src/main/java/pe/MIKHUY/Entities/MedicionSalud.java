package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity para registrar el historial de mediciones de salud del estudiante
 */
@Entity
@Table(name = "medicion_salud")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicionSalud {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso; // en kg

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal talla; // en cm

    @Column(precision = 5, scale = 2)
    private BigDecimal imc; // Índice de Masa Corporal

    @Column(length = 30)
    private String estadoNutricional; // Bajo peso, Normal, Sobrepeso, Obesidad

    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas; // Observaciones del profesor o sistema

    /**
     * Calcular IMC automáticamente
     * IMC = peso (kg) / (talla (m))^2
     */
    @PrePersist
    @PreUpdate
    public void calcularIMC() {
        if (peso != null && talla != null && talla.compareTo(BigDecimal.ZERO) > 0) {
            // Convertir talla de cm a metros
            BigDecimal tallaMetros = talla.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            // Calcular IMC
            this.imc = peso.divide(
                    tallaMetros.multiply(tallaMetros),
                    2,
                    RoundingMode.HALF_UP
            );

            // Determinar estado nutricional
            this.estadoNutricional = determinarEstadoNutricional(this.imc);
        }
    }

    /**
     * Determinar estado nutricional según IMC
     * Basado en clasificación OMS para adolescentes
     */
    private String determinarEstadoNutricional(BigDecimal imc) {
        if (imc == null) return "Desconocido";

        double imcValue = imc.doubleValue();

        if (imcValue < 18.5) {
            return "Bajo peso";
        } else if (imcValue >= 18.5 && imcValue < 25) {
            return "Normal";
        } else if (imcValue >= 25 && imcValue < 30) {
            return "Sobrepeso";
        } else {
            return "Obesidad";
        }
    }
}
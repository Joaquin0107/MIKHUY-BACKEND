package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reto7dias_registros",
        uniqueConstraints = @UniqueConstraint(columnNames = {"sesion_id", "dia_numero", "momento_dia"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reto7DiasRegistro {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionJuego sesion;

    @Column(name = "dia_numero", nullable = false)
    private Integer diaNumero; // 1-7

    @Enumerated(EnumType.STRING)
    @Column(name = "momento_dia", nullable = false, length = 20)
    private MomentoDiaEnum momentoDia;

    @Column(name = "alimentos_frutas")
    private Integer alimentosFrutas = 0;

    @Column(name = "alimentos_verduras")
    private Integer alimentosVerduras = 0;

    @Column(name = "alimentos_proteinas")
    private Integer alimentosProteinas = 0;

    @Column(name = "alimentos_carbohidratos")
    private Integer alimentosCarbohidratos = 0;

    @Column(name = "alimentos_lacteos")
    private Integer alimentosLacteos = 0;

    @Column(name = "alimentos_dulces")
    private Integer alimentosDulces = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "emocion", length = 20)
    private EmocionEnum emocion;

    @Column(name = "calorias_estimadas")
    private Integer caloriasEstimadas;

    @Column(name = "notas", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    // Enums
    public enum MomentoDiaEnum {
        Desayuno,
        Almuerzo,
        Cena
    }

    public enum EmocionEnum {
        feliz,
        normal,
        triste,
        estresado,
        ansioso
    }
}
package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "amistades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"estudiante1_id", "estudiante2_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Convención: estudiante1Id < estudiante2Id (orden por UUID) para evitar duplicados (A,B) y (B,A)
    @Column(name = "estudiante1_id", nullable = false)
    private UUID estudiante1Id;

    @Column(name = "estudiante2_id", nullable = false)
    private UUID estudiante2Id;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }
}
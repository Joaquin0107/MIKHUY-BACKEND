package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.Beneficio;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, UUID> {

    // Buscar beneficios activos
    List<Beneficio> findByActivoTrue();

    // Buscar por categoría
    List<Beneficio> findByCategoria(String categoria);

    // Buscar por categoría y activos
    List<Beneficio> findByCategoriaAndActivoTrue(String categoria);

    // Buscar beneficios con stock disponible
    List<Beneficio> findByActivoTrueAndStockGreaterThan(Integer stock);

    // Buscar beneficios por rango de puntos
    List<Beneficio> findByPuntosRequeridosLessThanEqualAndActivoTrue(Integer puntosMaximos);

    // Buscar beneficios ordenados por puntos (más baratos primero)
    List<Beneficio> findByActivoTrueOrderByPuntosRequeridosAsc();

    // Beneficios más canjeados
    @Query("SELECT b FROM Beneficio b " +
            "JOIN b.canjes c " +
            "GROUP BY b " +
            "ORDER BY COUNT(c) DESC")
    List<Beneficio> findMostExchanged();

    // Contar beneficios por categoría
    long countByCategoria(String categoria);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}
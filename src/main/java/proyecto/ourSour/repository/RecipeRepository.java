package proyecto.ourSour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.ourSour.model.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>{
    //UNA SOLA CONSULTA TRAE RECETA + INGREDIENTES + PRODUCTO (EVITA EL PROBLEMA N+1)
    @Query("""
            select distinct r from Recipe r
            left join fetch r.ingredients ri
            left join fetch ri.ingredient
            left join fetch r.product
            """) 
    List<Recipe> findAllWithDetails();

    @Query("""
            select r from Recipe r
            left join fetch r.ingredients ri
            left join fetch ri.ingredient
            left join fetch r.product
            where r.id = :id
            """) 
    List<Recipe> findByWithDetails(@Param("id") Long id);



}

package proyecto.ourSour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.ourSour.model.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    List<Ingredient> findAllByOrderByCategoryAscNameAsc();
    Optional<Ingredient> findByNameIgnoreCase(String name);

}

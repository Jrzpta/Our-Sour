package proyecto.ourSour.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import proyecto.ourSour.dto.Dtos.ProductDto;
import proyecto.ourSour.dto.Dtos.RecipeMatchDto;
import proyecto.ourSour.model.Recipe;
import proyecto.ourSour.model.RecipeIngredient;

/**
 * Motor de sugerencia (núcleo del proyecto): compara los ingredientes que el usuario
 * tiene contra los que pide cada receta y calcula el porcentaje de compatibilidad.
 * No toca la base de datos, por eso se puede probar con pruebas unitarias simples.
 */
@Component
public class RecipeMatcher {
 
    public RecipeMatchDto evaluate(Recipe recipe, Set<Long> availableIds) {
        List<String> missing = new ArrayList<>();
        int matched = 0;
        for (RecipeIngredient ri : recipe.getIngredients()) {
            if (availableIds.contains(ri.getIngredient().getId())) {
                matched++;
            } else {
                missing.add(ri.getIngredient().getName());
            }
        }
        int total = recipe.getIngredients().size();
        int percent = total == 0 ? 0 : Math.round(matched * 100f / total);
 
        boolean sellable = recipe.getProduct() != null && recipe.getProduct().isActive();
        ProductDto product = sellable ? ProductDto.from(recipe.getProduct()) : null;
 
        return new RecipeMatchDto(recipe.getId(), recipe.getTitle(), percent,
                matched, total, missing, product);
    }
}


package proyecto.ourSour.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.IngredientLine;
import proyecto.ourSour.dto.Dtos.ProductDto;
import proyecto.ourSour.dto.Dtos.RecipeDetailDto;
import proyecto.ourSour.dto.Dtos.RecipeMatchDto;
import proyecto.ourSour.dto.Dtos.RecipeSearchRequest;
import proyecto.ourSour.dto.Dtos.RecipeSearchResponse;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.Ingredient;
import proyecto.ourSour.model.Recipe;
import proyecto.ourSour.model.RecipeView;
import proyecto.ourSour.model.SearchLog;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.IngredientRepository;
import proyecto.ourSour.repository.RecipeRepository;
import proyecto.ourSour.repository.RecipeViewRepository;
import proyecto.ourSour.repository.SearchLogRepository;
import proyecto.ourSour.repository.UserRepository;



@Service
@RequiredArgsConstructor
public class RecipeService {
 
    private static final int DEFAULT_MIN_MATCH = 50;
 
    private final RecipeRepository recipes;
    private final IngredientRepository ingredients;
    private final UserRepository users;
    private final SearchLogRepository searchLogs;
    private final RecipeViewRepository recipeViews;
    private final RecipeMatcher matcher;
 
    /** CU-02: recetas compatibles con los ingredientes declarados. */
    @Transactional
    public RecipeSearchResponse search(String email, RecipeSearchRequest req) {
        User user = users.getByEmail(email);
 
        // Paso 3 del CU-02: validar consistencia contra el catálogo maestro.
        Set<Long> ids = new HashSet<>(req.ingredientIds());
        List<Ingredient> declared = ingredients.findAllById(ids);
        if (declared.size() != ids.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Hay ingredientes que no existen en el catálogo");
        }
        int minMatch = req.minMatch() == null ? DEFAULT_MIN_MATCH : req.minMatch();
 
        List<RecipeMatchDto> withAnyMatch = recipes.findAllWithDetails().stream()
                .map(r -> matcher.evaluate(r, ids))
                .filter(m -> m.matchedCount() > 0)
                .toList();
 
        // Orden: mayor compatibilidad primero; a igualdad, las que tienen producto Our Sour; luego A-Z.
        List<RecipeMatchDto> compatible = withAnyMatch.stream()
                .filter(m -> m.matchPercent() >= minMatch)
                .sorted(Comparator.comparingInt(RecipeMatchDto::matchPercent).reversed()
                        .thenComparing(m -> m.product() == null)
                        .thenComparing(RecipeMatchDto::title))
                .toList();
 
        registerSearch(user, declared);   // alimenta las métricas del CU-06
 
        if (!compatible.isEmpty()) {
            return new RecipeSearchResponse(compatible, List.of(), null);
        }
        // Flujo alternativo: sin coincidencias -> sugerir productos Our Sour relacionados.
        List<ProductDto> suggested = withAnyMatch.stream()
                .map(RecipeMatchDto::product)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        return new RecipeSearchResponse(List.of(), suggested,
                "No hay recetas compatibles con tus ingredientes. Estos productos Our Sour podrían ayudarte.");
    }
 
    /** CU-03: detalle de la receta, marcando qué ingredientes tiene o le faltan al usuario. */
    @Transactional
    public RecipeDetailDto detail(String email, Long recipeId, List<Long> availableIds) {
        User user = users.getByEmail(email);
        Recipe recipe = recipes.findByIdWithDetails(recipeId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Receta no encontrada"));
        Set<Long> have = availableIds == null ? Set.of() : new HashSet<>(availableIds);
 
        List<IngredientLine> lines = recipe.getIngredients().stream()
                .map(ri -> new IngredientLine(ri.getIngredient().getName(), ri.getQuantity(),
                        have.contains(ri.getIngredient().getId())))
                .toList();
        List<String> steps = Arrays.stream(recipe.getSteps().split("\n"))
                .filter(s -> !s.isBlank()).toList();
 
        RecipeView view = new RecipeView();
        view.setRecipe(recipe);
        view.setUser(user);
        recipeViews.save(view);          // alimenta "recetas más consultadas"
 
        boolean sellable = recipe.getProduct() != null && recipe.getProduct().isActive();
        return new RecipeDetailDto(recipe.getId(), recipe.getTitle(), recipe.getDescription(),
                steps, lines, sellable ? ProductDto.from(recipe.getProduct()) : null);
    }
 
    private void registerSearch(User user, List<Ingredient> declared) {
        String key = declared.stream().map(Ingredient::getName).sorted()
                .collect(Collectors.joining(" + "));
        SearchLog log = new SearchLog();
        log.setUser(user);
        log.setIngredientKey(key.length() > 500 ? key.substring(0, 500) : key);
        log.getIngredients().addAll(declared);
        searchLogs.save(log);
    }
}


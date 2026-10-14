package proyecto.ourSour.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.MetricsResponse;
import proyecto.ourSour.dto.MetricItem;
import proyecto.ourSour.repository.RecipeViewRepository;
import proyecto.ourSour.repository.SearchLogRepository;

@Service
@RequiredArgsConstructor
public class MetricsService {
 
    private static final Pageable TOP_10 = PageRequest.of(0, 10);
 
    private final SearchLogRepository searchLogs;
    private final RecipeViewRepository recipeViews;
 
    /** CU-06: datos agregados de los últimos N días. */
    @Transactional(readOnly = true)
    public MetricsResponse summary(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
 
        List<MetricItem> ingredients = searchLogs.topIngredients(since, TOP_10);
        List<MetricItem> recipes = recipeViews.topRecipes(since, TOP_10);
        List<MetricItem> combos = searchLogs.topCombinations(since, TOP_10);
        List<MetricItem> perDay = searchLogs.searchesPerDay(since).stream()
                .map(d -> new MetricItem(d.getLabel(), d.getTotal())).toList();
 
        // Flujo alternativo: sin datos en el período.
        String message = (ingredients.isEmpty() && recipes.isEmpty())
                ? "No hay información suficiente en los últimos " + days + " días" : null;
        return new MetricsResponse(days, ingredients, recipes, combos, perDay, message);
    }
}


package proyecto.ourSour.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.ourSour.dto.MetricItem;
import proyecto.ourSour.model.RecipeView;

public interface RecipeViewRepository extends JpaRepository<RecipeView, Long> {
    //RECETAS MAS CONSULTADAS
    @Query ("""
            select new proyecto.ourSour.dto.MetricItem(r.title, count(v))
            from RecipeView v join v.recipe r
            where v.createdAt >= :since
            group by r.id, r.title
            order by count(v) desc
            """)
    List<MetricItem> topRecipes(@Param ("since") LocalDateTime since, Pageable pageable);

}

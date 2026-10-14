package proyecto.ourSour.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyecto.ourSour.dto.MetricItem;
import proyecto.ourSour.model.SearchLog;

public interface SearchLogRepository extends JpaRepository<SearchLog, Long> {
    //INGREDIENTES SELECCIONADOS
    @Query("""
            select new proyecto.ourSour.dto.MetricItem(i.name, count(s))
            from SearchLog s join s.ingredients i
            where s.createdAt >= :since
            group by i.id, i.name
            order by count(s) desc
            """)
    List<MetricItem> topIngredients(@Param ("since") LocalDateTime since, Pageable pageable);

    //COMBINACIONES DE INGREDIENTES MAS REPETIDAS
    @Query ("""
            select new proyecto.ourSour.dto.MetricItem(s.ingredientKey, count(s))
            from SearchLog s
            where s.createdAt >= :since
            group by s.ingredientKey
            order by count(s) desc
            """)
    List<MetricItem> topCombinations(@Param ("since")LocalDateTime since, Pageable pageable);

    //TENDENCIA: BUSQUEDAS POR DIA
    @Query (value = """
            SELECT DATE_FORMAT(created_at, '%Y-%m-%d') AS label, COUNT(*) AS total
            FROM search_log
            WHERE created_at >= :since
            GROUP BY DATE_FORMAT(created_at, '%Y-%m-%d')
            ORDER BY label
            """, nativeQuery = true)
    List<DailyCount> searchesPerDay(@Param ("since") LocalDateTime since);

    interface DailyCount{
        String getLabel();

        Long getTotal();
    }

}

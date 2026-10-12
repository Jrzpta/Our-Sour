package proyecto.ourSour.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "search_log")
@Getter 
@Setter 
public class SearchLog {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "user_id")
    private User user;

    //COMBINACION LEGIBLE, EJEMPLO: "GIN + HIELO + LIMON" (PARA CONTAR COMBINACIONES FRECUENTES)
    @Column (name = "ingredient_key", nullable = false, length = 500)
    private String ingredientKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToMany 
    @JoinTable (name = "search_log_ingredients", 
                joinColumns = @JoinColumn(name = "search_log_id"), 
                inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private Set<Ingredient> ingredients = new HashSet<>();

}

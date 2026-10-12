package proyecto.ourSour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "ingredients")
@Getter 
@Setter 
public class Ingredient {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, unique = true, length = 80)
    private String name;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 30)
    private IngredientCategory category;

}

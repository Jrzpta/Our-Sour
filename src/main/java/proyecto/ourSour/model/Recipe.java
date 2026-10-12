package proyecto.ourSour.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "recipes")
@Getter 
@Setter 
public class Recipe {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 120)
    private String title;

    @Column (length = 500)
    private String description;

    //PASOS DE PREPARACION, UNO POR LINEA
    @Column (nullable = false, columnDefinition = "TEXT")
    private String steps;

    //PRODUCTO OURSOUR ASOCIADO A LA RECETA (PUEDE SER NULL)
    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn (name = "product_id")
    private Product product;

    
    @OneToMany (mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public void addIngredient(Ingredient ingredient, String quantity){
        RecipeIngredient ri = new RecipeIngredient();
        ri.setRecipe(this);
        ri.setIngredient(ingredient);
        ri.setQuantity(quantity);
        ingredients.add(ri);
    }

}

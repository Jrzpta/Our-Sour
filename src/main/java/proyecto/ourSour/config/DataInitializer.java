package proyecto.ourSour.config;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.model.Ingredient;
import proyecto.ourSour.model.IngredientCategory;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.model.Recipe;
import proyecto.ourSour.model.Role;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.IngredientRepository;
import proyecto.ourSour.repository.ProductRepository;
import proyecto.ourSour.repository.RecipeRepository;
import proyecto.ourSour.repository.UserRepository;

/**
 * Carga datos FICTICIOS al primer arranque (el anexo exige que cada equipo cree sus propios datos).
 * Solo inserta si las tablas están vacías, así que es seguro reiniciar la aplicación.
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
 
    private final UserRepository users;
    private final IngredientRepository ingredients;
    private final ProductRepository products;
    private final RecipeRepository recipes;
    private final PasswordEncoder encoder;
 
    @Override
    @Transactional
    public void run(String... args) {
        if (!users.existsByEmail("admin@oursour.cl")) {
            User admin = new User();
            admin.setEmail("admin@oursour.cl");
            admin.setFullName("Administrador Our Sour");
            admin.setPasswordHash(encoder.encode("Admin123!"));
            admin.setRole(Role.ADMIN);
            users.save(admin);
        }
        if (ingredients.count() == 0) {
            seedCatalog();
        }
    }
 
    private void seedCatalog() {
        // ---- Productos Our Sour (ficticios) ----
        Map<String, Product> prod = new HashMap<>();
        Object[][] productData = {
                {"Our Sour Limón", "Mezcla sour concentrada sabor limón, 500 ml", 6990, 50},
                {"Our Sour Maracuyá", "Mezcla sour concentrada sabor maracuyá, 500 ml", 7490, 40},
                {"Our Sour Frambuesa", "Mezcla sour concentrada sabor frambuesa, 500 ml", 7990, 30},
                {"Our Sour Jengibre", "Mezcla sour con jengibre y limón, 500 ml", 7490, 35},
                {"Our Sour Lima-Menta", "Mezcla sour lima y menta, 500 ml", 7290, 45}
        };
        for (Object[] d : productData) {
            Product p = new Product();
            p.setName((String) d[0]);
            p.setDescription((String) d[1]);
            p.setPrice(BigDecimal.valueOf((Integer) d[2]));
            p.setStock((Integer) d[3]);
            prod.put(p.getName(), products.save(p));
        }
 
        // ---- Catálogo de ingredientes ----
        Map<String, Ingredient> ing = new HashMap<>();
        String[][] ingredientData = {
                {"DESTILADO", "Gin"}, {"DESTILADO", "Vodka"}, {"DESTILADO", "Ron"},
                {"DESTILADO", "Pisco"}, {"DESTILADO", "Tequila"},
                {"FRUTA", "Limón"}, {"FRUTA", "Lima"}, {"FRUTA", "Frutilla"}, {"FRUTA", "Naranja"},
                {"JUGO", "Jugo de naranja"}, {"JUGO", "Jugo de piña"}, {"JUGO", "Jugo de arándano"},
                {"MIXER", "Agua tónica"}, {"MIXER", "Ginger ale"},
                {"MIXER", "Agua con gas"}, {"MIXER", "Bebida cola"},
                {"OTRO", "Hielo"}, {"OTRO", "Azúcar"}, {"OTRO", "Menta"}
        };
        for (String[] d : ingredientData) {
            saveIngredient(ing, d[1], IngredientCategory.valueOf(d[0]));
        }
        // Cada producto Our Sour también existe como ingrediente seleccionable.
        for (String name : prod.keySet()) {
            saveIngredient(ing, name, IngredientCategory.PRODUCTO_OUR_SOUR);
        }
 
        // ---- Recetas ----
        recipe(ing, prod, "Gin Sour", "Clásico cítrico con base de gin.",
                "Llena una coctelera con hielo.\nAgrega gin y Our Sour Limón.\nAgita 15 segundos y sirve colado.",
                "Our Sour Limón", "Gin:60 ml", "Limón:2 rodajas", "Our Sour Limón:30 ml", "Hielo:c/n");
        recipe(ing, prod, "Gin Tonic", "Simple y refrescante.",
                "Llena un vaso alto con hielo.\nAgrega el gin.\nCompleta con agua tónica y una rodaja de limón.",
                "Our Sour Limón", "Gin:50 ml", "Agua tónica:150 ml", "Limón:1 rodaja", "Hielo:c/n");
        recipe(ing, prod, "Pisco Sour", "El trago nacional con Our Sour como base cítrica.",
                "Coloca hielo en la coctelera.\nAgrega pisco, Our Sour Limón y azúcar.\nAgita fuerte y sirve en copa.",
                "Our Sour Limón", "Pisco:60 ml", "Our Sour Limón:30 ml", "Azúcar:10 g", "Hielo:c/n");
        recipe(ing, prod, "Mojito", "Ron, menta y lima.",
                "Machaca la menta con azúcar y lima.\nAgrega ron y hielo.\nCompleta con agua con gas.",
                "Our Sour Lima-Menta", "Ron:50 ml", "Lima:1 unidad", "Menta:8 hojas",
                "Azúcar:10 g", "Agua con gas:100 ml", "Hielo:c/n");
        recipe(ing, prod, "Moscow Mule", "Vodka con notas de jengibre.",
                "Llena un vaso con hielo.\nAgrega vodka y Our Sour Jengibre.\nCompleta con ginger ale y exprime lima.",
                "Our Sour Jengibre", "Vodka:50 ml", "Our Sour Jengibre:30 ml", "Ginger ale:100 ml",
                "Lima:1/2 unidad", "Hielo:c/n");
        recipe(ing, prod, "Maracuyá Sour", "Versión tropical del sour.",
                "Agrega hielo a la coctelera.\nMezcla pisco y Our Sour Maracuyá.\nAgita y sirve colado.",
                "Our Sour Maracuyá", "Pisco:60 ml", "Our Sour Maracuyá:40 ml", "Hielo:c/n");
        recipe(ing, prod, "Mocktail de Frutilla", "Sin alcohol, ideal para todos.",
                "Machaca las frutillas.\nAgrega Our Sour Frambuesa y hielo.\nCompleta con agua con gas.",
                "Our Sour Frambuesa", "Frutilla:5 unidades", "Limón:1 rodaja",
                "Our Sour Frambuesa:30 ml", "Agua con gas:120 ml", "Hielo:c/n");
        recipe(ing, prod, "Cuba Libre", "Ron, cola y lima.",
                "Llena un vaso con hielo.\nAgrega ron y jugo de lima.\nCompleta con bebida cola.",
                null, "Ron:50 ml", "Bebida cola:150 ml", "Lima:1/2 unidad", "Hielo:c/n");
    }
 
    private void saveIngredient(Map<String, Ingredient> map, String name, IngredientCategory category) {
        Ingredient i = new Ingredient();
        i.setName(name);
        i.setCategory(category);
        map.put(name, ingredients.save(i));
    }
 
    /** Cada línea tiene el formato "Ingrediente:cantidad". */
    private void recipe(Map<String, Ingredient> ing, Map<String, Product> prod, String title,
                        String description, String steps, String productName, String... lines) {
        Recipe r = new Recipe();
        r.setTitle(title);
        r.setDescription(description);
        r.setSteps(steps);
        r.setProduct(productName == null ? null : prod.get(productName));
        for (String line : lines) {
            String[] parts = line.split(":");
            r.addIngredient(ing.get(parts[0]), parts[1]);
        }
        recipes.save(r);
    }
}


package proyecto.ourSour.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import proyecto.ourSour.model.Ingredient;
import proyecto.ourSour.model.IngredientCategory;
import proyecto.ourSour.model.OrderStatus;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.model.Role;

public final class Dtos {
 
    private Dtos() {
    }
 
    // ---------- Autenticación (CU-07) ----------
    public record RegisterRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, max = 72) String password,
            @NotBlank String fullName) {
    }
 
    public record LoginRequest(@NotBlank @Email String email, @NotBlank String password) {
    }
 
    public record AuthResponse(String token, String email, String fullName, Role role) {
    }
 
    // ---------- Ingredientes (CU-01) ----------
    public record IngredientDto(Long id, String name, IngredientCategory category) {
        public static IngredientDto from(Ingredient i) {
            return new IngredientDto(i.getId(), i.getName(), i.getCategory());
        }
    }
 
    public record IngredientRequest(
            @NotBlank @Size(max = 80) String name,
            @NotNull IngredientCategory category) {
    }
 
    public record IngredientIdsRequest(@NotNull List<Long> ingredientIds) {
    }
 
    // ---------- Productos (CU-05) ----------
    public record ProductRequest(
            @NotBlank @Size(max = 120) String name,
            @Size(max = 500) String description,
            @NotNull @Positive BigDecimal price,
            @NotNull @PositiveOrZero Integer stock) {
    }
 
    public record ProductDto(Long id, String name, String description,
                             BigDecimal price, int stock, boolean active) {
        public static ProductDto from(Product p) {
            if (p == null) {
                return null;
            }
            return new ProductDto(p.getId(), p.getName(), p.getDescription(),
                    p.getPrice(), p.getStock(), p.isActive());
        }
    }
 
    // ---------- Recetas (CU-02 y CU-03) ----------
    public record RecipeSearchRequest(
            @NotEmpty List<Long> ingredientIds,
            @Min(0) @Max(100) Integer minMatch) {
    }
 
    public record RecipeMatchDto(Long recipeId, String title, int matchPercent,
                                 int matchedCount, int totalCount,
                                 List<String> missingIngredients, ProductDto product) {
    }
 
    public record RecipeSearchResponse(List<RecipeMatchDto> recipes,
                                       List<ProductDto> suggestedProducts,
                                       String message) {
    }
 
    public record IngredientLine(String name, String quantity, boolean available) {
    }
 
    public record RecipeDetailDto(Long id, String title, String description,
                                  List<String> steps, List<IngredientLine> ingredients,
                                  ProductDto product) {
    }
 
    // ---------- Carro (CU-04) ----------
    public record AddToCartRequest(@NotNull Long productId, @Min(1) Integer quantity) {
    }
 
    public record QuantityRequest(@NotNull @Min(1) Integer quantity) {
    }
 
    public record CartItemDto(Long productId, String name, BigDecimal unitPrice,
                              int quantity, BigDecimal subtotal) {
    }
 
    public record CartDto(List<CartItemDto> items, BigDecimal subtotal,
                          BigDecimal shippingCost, BigDecimal total) {
    }
 
    // ---------- Despacho, pago y pedido (CU-08) ----------
    public record CheckoutRequest(
            @NotBlank String address,
            @NotBlank String city,
            @NotBlank @Pattern(regexp = "^[0-9 ]{12,19}$", message = "Tarjeta sandbox inválida") String cardNumber) {
    }
 
    public record OrderItemDto(String productName, BigDecimal unitPrice,
                               int quantity, BigDecimal subtotal) {
    }
 
    public record OrderDto(Long id, OrderStatus status, BigDecimal subtotal,
                           BigDecimal shippingCost, BigDecimal total,
                           String address, String city, String paymentReference,
                           LocalDateTime createdAt, List<OrderItemDto> items) {
    }
 
    // ---------- Métricas (CU-06) ----------
    public record MetricsResponse(int days, List<MetricItem> topIngredients,
                                  List<MetricItem> topRecipes,
                                  List<MetricItem> frequentCombinations,
                                  List<MetricItem> searchesPerDay, String message) {
    }
}

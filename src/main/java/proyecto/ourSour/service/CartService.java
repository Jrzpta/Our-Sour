package proyecto.ourSour.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.AddToCartRequest;
import proyecto.ourSour.dto.Dtos.CartDto;
import proyecto.ourSour.dto.Dtos.CartItemDto;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.CartItem;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.CartItemRepository;
import proyecto.ourSour.repository.ProductRepository;
import proyecto.ourSour.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CartService {
 
    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final UserRepository users;
 
    @Value("${oursour.shipping.free-threshold}")
    private BigDecimal freeShippingThreshold;
 
    @Value("${oursour.shipping.flat-cost}")
    private BigDecimal flatShippingCost;
 
    @Transactional(readOnly = true)
    public CartDto get(String email) {
        return toDto(cartItems.findByUser(users.getByEmail(email)));
    }
 
    /** CU-04: agrega el producto con cantidad 1 por defecto (o suma si ya estaba en el carro). */
    @Transactional
    public CartDto add(String email, AddToCartRequest req) {
        User user = users.getByEmail(email);
        Product product = activeProduct(req.productId());
        int qty = req.quantity() == null ? 1 : req.quantity();
 
        CartItem item = cartItems.findByUserAndProduct(user, product).orElseGet(() -> {
            CartItem c = new CartItem();
            c.setUser(user);
            c.setProduct(product);
            return c;
        });
        item.setQuantity(item.getQuantity() + qty);
        checkStock(product, item.getQuantity());
        cartItems.save(item);
        return toDto(cartItems.findByUser(user));
    }
 
    /** Flujo alternativo CU-04: modificar la cantidad. */
    @Transactional
    public CartDto updateQuantity(String email, Long productId, int quantity) {
        User user = users.getByEmail(email);
        CartItem item = findItem(user, productId);
        checkStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        cartItems.save(item);
        return toDto(cartItems.findByUser(user));
    }
 
    /** Flujo alternativo CU-04: eliminar el producto del carro. */
    @Transactional
    public CartDto remove(String email, Long productId) {
        User user = users.getByEmail(email);
        cartItems.delete(findItem(user, productId));
        return toDto(cartItems.findByUser(user));
    }
 
    /** Regla de despacho: gratis desde el umbral; si no, tarifa plana; carro vacío = 0. */
    public BigDecimal calculateShipping(BigDecimal subtotal) {
        if (subtotal.signum() == 0 || subtotal.compareTo(freeShippingThreshold) >= 0) {
            return BigDecimal.ZERO;
        }
        return flatShippingCost;
    }
 
    public CartDto toDto(List<CartItem> items) {
        List<CartItemDto> lines = items.stream()
                .map(i -> new CartItemDto(i.getProduct().getId(), i.getProduct().getName(),
                        i.getProduct().getPrice(), i.getQuantity(),
                        i.getProduct().getPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();
        BigDecimal subtotal = lines.stream().map(CartItemDto::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal shipping = calculateShipping(subtotal);
        return new CartDto(lines, subtotal, shipping, subtotal.add(shipping));
    }
 
    private Product activeProduct(Long id) {
        Product p = products.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        if (!p.isActive()) {
            throw new ApiException(HttpStatus.CONFLICT, "El producto ya no está disponible");
        }
        return p;
    }
 
    private CartItem findItem(User user, Long productId) {
        Product p = products.findById(productId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
        return cartItems.findByUserAndProduct(user, p)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "El producto no está en el carro"));
    }
 
    private void checkStock(Product p, int requested) {
        if (requested > p.getStock()) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "Stock insuficiente de " + p.getName() + " (disponible: " + p.getStock() + ")");
        }
    }
}


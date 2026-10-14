package proyecto.ourSour.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.CheckoutRequest;
import proyecto.ourSour.dto.Dtos.OrderDto;
import proyecto.ourSour.dto.Dtos.OrderItemDto;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.CartItem;
import proyecto.ourSour.model.OrderItem;
import proyecto.ourSour.model.OrderStatus;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.model.PurchaseOrder;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.CartItemRepository;
import proyecto.ourSour.repository.ProductRepository;
import proyecto.ourSour.repository.PurchaseOrderRepository;
import proyecto.ourSour.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class OrderService {
 
    private final CartItemRepository cartItems;
    private final ProductRepository products;
    private final PurchaseOrderRepository orders;
    private final UserRepository users;
    private final CartService cartService;
    private final PaymentSimulator payments;
 
    /**
     * CU-08: Dirección/despacho -> checkout -> pago sandbox -> pedido.
     * Es una sola transacción: si el pago falla o no hay stock, no se guarda nada.
     */
    @Transactional
    public OrderDto checkout(String email, CheckoutRequest req) {
        User user = users.getByEmail(email);
        List<CartItem> items = cartItems.findByUser(user);
        if (items.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "El carro está vacío");
        }
 
        BigDecimal subtotal = BigDecimal.ZERO;
        for (CartItem item : items) {
            Product p = item.getProduct();
            if (!p.isActive() || item.getQuantity() > p.getStock()) {
                throw new ApiException(HttpStatus.CONFLICT, "Sin stock suficiente de " + p.getName());
            }
            subtotal = subtotal.add(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        BigDecimal shipping = cartService.calculateShipping(subtotal);
        BigDecimal total = subtotal.add(shipping);
 
        PaymentSimulator.PaymentResult payment = payments.charge(req.cardNumber(), total);
        if (!payment.approved()) {
            throw new ApiException(HttpStatus.PAYMENT_REQUIRED, "Pago rechazado en sandbox. Intenta con otra tarjeta.");
        }
 
        PurchaseOrder order = new PurchaseOrder();
        order.setUser(user);
        order.setStatus(OrderStatus.CONFIRMADO);
        order.setSubtotal(subtotal);
        order.setShippingCost(shipping);
        order.setTotal(total);
        order.setAddress(req.address().trim());
        order.setCity(req.city().trim());
        order.setPaymentReference(payment.reference());
 
        for (CartItem item : items) {
            Product p = item.getProduct();
            OrderItem line = new OrderItem();
            line.setOrder(order);
            line.setProduct(p);
            line.setProductName(p.getName());
            line.setUnitPrice(p.getPrice());
            line.setQuantity(item.getQuantity());
            order.getItems().add(line);
 
            p.setStock(p.getStock() - item.getQuantity());   // descuenta el stock simulado
            products.save(p);
        }
        orders.save(order);
        cartItems.deleteByUser(user);                         // el carro queda vacío
        return toDto(order);
    }
 
    @Transactional(readOnly = true)
    public List<OrderDto> myOrders(String email) {
        User user = users.getByEmail(email);
        return orders.findByUserOrderByCreatedAtDesc(user).stream().map(this::toDto).toList();
    }
 
    private OrderDto toDto(PurchaseOrder o) {
        List<OrderItemDto> lines = o.getItems().stream()
                .map(i -> new OrderItemDto(i.getProductName(), i.getUnitPrice(), i.getQuantity(),
                        i.getUnitPrice().multiply(BigDecimal.valueOf(i.getQuantity()))))
                .toList();
        return new OrderDto(o.getId(), o.getStatus(), o.getSubtotal(), o.getShippingCost(),
                o.getTotal(), o.getAddress(), o.getCity(), o.getPaymentReference(),
                o.getCreatedAt(), lines);
    }
}

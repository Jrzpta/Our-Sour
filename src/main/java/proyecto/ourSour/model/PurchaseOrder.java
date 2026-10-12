package proyecto.ourSour.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table (name = "orders")
@Getter 
@Setter 
public class PurchaseOrder {
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "user_id")
    private User user;

    @Enumerated (EnumType.STRING)
    @Column (nullable = false, length = 20)
    private OrderStatus status = OrderStatus.CONFIRMADO;

    @Column (nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column (name = "shipping_cost",nullable = false, precision = 12, scale = 2)
    private BigDecimal shippingCost;

    @Column (nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column (nullable = false, length = 200)
    private String address;

    @Column (nullable = false, length = 80)
    private String city;

    @Column (name = "payment_reference", length = 40)
    private String paymentReference;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany (mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    

}

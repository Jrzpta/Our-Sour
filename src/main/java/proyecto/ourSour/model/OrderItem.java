package proyecto.ourSour.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "order_items")
@Getter 
@Setter 
public class OrderItem {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "order_id")
    private PurchaseOrder order;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn (name = "product_id")
    private Product product;

    @Column (name = "product_name", nullable = false, length = 120)
    private String productName;

    @Column (name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column (nullable = false)
    private int quantity;

}

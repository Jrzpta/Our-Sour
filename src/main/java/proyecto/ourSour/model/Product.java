package proyecto.ourSour.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table (name = "products")
@Getter 
@Setter 
public class Product {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false, length = 120)
    private String name;

    @Column (length = 500)
    private String description;

    @Column (nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // STOCK SIMULADO (CU-05)
    @Column (nullable = false)
    private int Stock;

    // DESACTIVAR UN PRODUCTO NO LO BORRA, CONSERVA EL HISTORIAL DE PEDIDOS
    @Column (nullable = false)
    private boolean active = true;
    

}

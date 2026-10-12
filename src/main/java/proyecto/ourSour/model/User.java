package proyecto.ourSour.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Table(name = "users")
@Getter 
@Setter 
public class User {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column (name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false, length = 120)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column (nullable = false, length = 20)
    private Role role = Role.CONSUMER;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();


    //INGREDIENTES FRECUENTES, QUE EL USUARIO DECLARÓ TENER (CU-01)
    @ManyToMany
    @JoinTable(name = "user_ingredient",
        joinColumns = @JoinColumn (name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
        private Set<Ingredient> savedIngredients = new HashSet<>();



}

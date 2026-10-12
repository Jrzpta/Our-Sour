package proyecto.ourSour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.ourSour.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByActiveTrueOrderByNameAsc();

    List<Product> findAllByOrderByNameAsc();

}

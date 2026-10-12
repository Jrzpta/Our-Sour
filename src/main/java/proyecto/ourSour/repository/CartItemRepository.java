package proyecto.ourSour.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.ourSour.model.CartItem;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.model.User;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUser(User user);

    Optional <CartItem> findByUserAndProduct(User user, Product product);

    void deleteByUser(User user);

}

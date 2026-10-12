package proyecto.ourSour.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyecto.ourSour.model.PurchaseOrder;
import proyecto.ourSour.model.User;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long>{
    List<PurchaseOrder> findByUserOrderByCreatedAtDesc(User user);

}

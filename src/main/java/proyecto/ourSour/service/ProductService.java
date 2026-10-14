package proyecto.ourSour.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.ProductDto;
import proyecto.ourSour.dto.Dtos.ProductRequest;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.Product;
import proyecto.ourSour.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class ProductService {
 
    private final ProductRepository products;
 
    /** Catálogo visible para el consumidor: solo productos activos. */
    @Transactional(readOnly = true)
    public List<ProductDto> listActive() {
        return products.findByActiveTrueOrderByNameAsc().stream().map(ProductDto::from).toList();
    }
 
    /** Vista del administrador: incluye productos desactivados. */
    @Transactional(readOnly = true)
    public List<ProductDto> listAll() {
        return products.findAllByOrderByNameAsc().stream().map(ProductDto::from).toList();
    }
 
    @Transactional
    public ProductDto create(ProductRequest req) {
        Product p = new Product();
        apply(p, req);
        return ProductDto.from(products.save(p));
    }
 
    @Transactional
    public ProductDto update(Long id, ProductRequest req) {
        Product p = find(id);
        apply(p, req);
        return ProductDto.from(products.save(p));
    }
 
    /** "Desactivar" en vez de borrar: los pedidos antiguos siguen apuntando al producto. */
    @Transactional
    public ProductDto deactivate(Long id) {
        Product p = find(id);
        p.setActive(false);
        return ProductDto.from(products.save(p));
    }
 
    private Product find(Long id) {
        return products.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }
 
    private void apply(Product p, ProductRequest req) {
        p.setName(req.name().trim());
        p.setDescription(req.description());
        p.setPrice(req.price());
        p.setStock(req.stock());
    }
}


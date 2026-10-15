package proyecto.ourSour.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.ProductDto;
import proyecto.ourSour.dto.Dtos.ProductRequest;
import proyecto.ourSour.service.ProductService;

/** Solo accesible con rol ADMIN (regla definida en SecurityConfig para /api/admin/**). */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {
 
    private final ProductService service;
 
    @GetMapping
    public List<ProductDto> listAll() {
        return service.listAll();
    }
 
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDto create(@Valid @RequestBody ProductRequest req) {
        return service.create(req);
    }
 
    @PutMapping("/{id}")
    public ProductDto update(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        return service.update(id, req);
    }
 
    @DeleteMapping("/{id}")
    public ProductDto deactivate(@PathVariable Long id) {
        return service.deactivate(id);
    }
}


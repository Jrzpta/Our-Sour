package proyecto.ourSour.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.ProductDto;
import proyecto.ourSour.service.ProductService;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
 
    private final ProductService service;
 
    @GetMapping
    public List<ProductDto> listActive() {
        return service.listActive();
    }
}

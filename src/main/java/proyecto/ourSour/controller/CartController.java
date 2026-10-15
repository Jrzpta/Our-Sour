package proyecto.ourSour.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.AddToCartRequest;
import proyecto.ourSour.dto.Dtos.CartDto;
import proyecto.ourSour.dto.Dtos.QuantityRequest;
import proyecto.ourSour.service.CartService;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
 
    private final CartService service;
 
    @GetMapping
    public CartDto get(Authentication auth) {
        return service.get(auth.getName());
    }
 
    @PostMapping("/items")
    public CartDto add(Authentication auth, @Valid @RequestBody AddToCartRequest req) {
        return service.add(auth.getName(), req);
    }
 
    @PutMapping("/items/{productId}")
    public CartDto updateQuantity(Authentication auth, @PathVariable Long productId,
                                  @Valid @RequestBody QuantityRequest req) {
        return service.updateQuantity(auth.getName(), productId, req.quantity());
    }
 
    @DeleteMapping("/items/{productId}")
    public CartDto remove(Authentication auth, @PathVariable Long productId) {
        return service.remove(auth.getName(), productId);
    }
}

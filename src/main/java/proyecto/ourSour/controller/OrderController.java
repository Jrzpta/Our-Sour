package proyecto.ourSour.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.CheckoutRequest;
import proyecto.ourSour.dto.Dtos.OrderDto;
import proyecto.ourSour.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
 
    private final OrderService service;
 
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto checkout(Authentication auth, @Valid @RequestBody CheckoutRequest req) {
        return service.checkout(auth.getName(), req);
    }
 
    @GetMapping
    public List<OrderDto> myOrders(Authentication auth) {
        return service.myOrders(auth.getName());
    }
}


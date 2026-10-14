package proyecto.ourSour.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.AuthResponse;
import proyecto.ourSour.dto.Dtos.LoginRequest;
import proyecto.ourSour.dto.Dtos.RegisterRequest;
import proyecto.ourSour.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
 
    private final AuthService authService;
 
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }
 
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }
}


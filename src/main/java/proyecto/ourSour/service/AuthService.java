package proyecto.ourSour.service;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.AuthResponse;
import proyecto.ourSour.dto.Dtos.LoginRequest;
import proyecto.ourSour.dto.Dtos.RegisterRequest;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.Role;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.UserRepository;
import proyecto.ourSour.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {
 
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
 
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmail(email)) {
            throw new ApiException(HttpStatus.CONFLICT, "El email ya está registrado");
        }
        User user = new User();
        user.setEmail(email);
        user.setFullName(req.fullName().trim());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(Role.CONSUMER);
        users.save(user);
        return toResponse(user);
    }
 
    public AuthResponse login(LoginRequest req) {
        User user = users.findByEmail(req.email().trim().toLowerCase())
                .filter(u -> encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        return toResponse(user);
    }
 
    private AuthResponse toResponse(User user) {
        return new AuthResponse(jwtService.generate(user), user.getEmail(),
                user.getFullName(), user.getRole());
    }
}


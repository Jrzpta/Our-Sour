package proyecto.ourSour.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.IngredientDto;
import proyecto.ourSour.dto.Dtos.IngredientIdsRequest;
import proyecto.ourSour.dto.Dtos.IngredientRequest;
import proyecto.ourSour.service.IngredientService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class IngredientController {
 
    private final IngredientService service;
 
    @GetMapping("/ingredients")
    public List<IngredientDto> catalog() {
        return service.catalog();
    }
 
    @PostMapping("/ingredients")
    @ResponseStatus(HttpStatus.CREATED)
    public IngredientDto create(@Valid @RequestBody IngredientRequest req) {
        return service.create(req);
    }
 
    @GetMapping("/me/ingredients")
    public List<IngredientDto> mine(Authentication auth) {
        return service.mine(auth.getName());
    }
 
    @PutMapping("/me/ingredients")
    public List<IngredientDto> replaceMine(Authentication auth,
                                           @Valid @RequestBody IngredientIdsRequest req) {
        return service.replaceMine(auth.getName(), req.ingredientIds());
    }
}


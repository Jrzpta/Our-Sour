package proyecto.ourSour.controller;


import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.RecipeDetailDto;
import proyecto.ourSour.dto.Dtos.RecipeSearchRequest;
import proyecto.ourSour.dto.Dtos.RecipeSearchResponse;
import proyecto.ourSour.service.RecipeService;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
 
    private final RecipeService service;
 
    @PostMapping("/search")
    public RecipeSearchResponse search(Authentication auth, @Valid @RequestBody RecipeSearchRequest req) {
        return service.search(auth.getName(), req);
    }
 
    @GetMapping("/{id}")
    public RecipeDetailDto detail(Authentication auth, @PathVariable Long id,
                                  @RequestParam(required = false) List<Long> ingredientIds) {
        return service.detail(auth.getName(), id, ingredientIds);
    }
}


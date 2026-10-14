package proyecto.ourSour.service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.IngredientDto;
import proyecto.ourSour.dto.Dtos.IngredientRequest;
import proyecto.ourSour.exception.ApiException;
import proyecto.ourSour.model.Ingredient;
import proyecto.ourSour.model.User;
import proyecto.ourSour.repository.IngredientRepository;
import proyecto.ourSour.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class IngredientService {
 
    private final IngredientRepository ingredients;
    private final UserRepository users;
 
    /** Catálogo completo, agrupable por categoría en la app. */
    @Transactional(readOnly = true)
    public List<IngredientDto> catalog() {
        return ingredients.findAllByOrderByCategoryAscNameAsc().stream()
                .map(IngredientDto::from).toList();
    }
 
    /** Flujo alternativo CU-01: el usuario agrega un ingrediente que no está en el listado. */
    @Transactional
    public IngredientDto create(IngredientRequest req) {
        String name = req.name().trim();
        Ingredient ingredient = ingredients.findByNameIgnoreCase(name).orElseGet(() -> {
            Ingredient i = new Ingredient();
            i.setName(name);
            i.setCategory(req.category());
            return ingredients.save(i);
        });
        return IngredientDto.from(ingredient);
    }
 
    @Transactional(readOnly = true)
    public List<IngredientDto> mine(String email) {
        User user = users.getByEmail(email);
        return user.getSavedIngredients().stream()
                .sorted(Comparator.comparing(Ingredient::getName))
                .map(IngredientDto::from).toList();
    }
 
    /** CU-01: reemplaza la selección completa del usuario por la nueva. */
    @Transactional
    public List<IngredientDto> replaceMine(String email, List<Long> ids) {
        User user = users.getByEmail(email);
        Set<Long> unique = new HashSet<>(ids);
        List<Ingredient> found = ingredients.findAllById(unique);
        if (found.size() != unique.size()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Hay ingredientes que no existen en el catálogo");
        }
        user.getSavedIngredients().clear();
        user.getSavedIngredients().addAll(found);
        users.save(user);
        return found.stream().sorted(Comparator.comparing(Ingredient::getName))
                .map(IngredientDto::from).toList();
    }
}


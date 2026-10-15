package proyecto.ourSour.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import proyecto.ourSour.dto.Dtos.MetricsResponse;
import proyecto.ourSour.service.MetricsService;

@RestController
@RequestMapping("/api/admin/metrics")
@RequiredArgsConstructor
public class AdminMetricsController {
 
    private final MetricsService service;
 
    @GetMapping
    public MetricsResponse summary(@RequestParam(defaultValue = "30") int days) {
        return service.summary(Math.max(1, Math.min(days, 365)));
    }
}


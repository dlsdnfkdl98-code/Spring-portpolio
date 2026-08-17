package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class ScreeningController {
    private final ScreeningService screeningService;

    public ScreeningController(ScreeningService screeningService) {
        this.screeningService = screeningService;
    }

    @GetMapping("/api/movies/{movieId}/screenings")
    public List<ScreeningResponse> getScreeningsByMovieId(@PathVariable Long movieId) {
        return screeningService.getScreeningsByMovieId(movieId);

    }
}

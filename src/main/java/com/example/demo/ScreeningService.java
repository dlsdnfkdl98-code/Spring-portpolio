package com.example.demo;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScreeningService {
    private final ScreeningRepository screeningRepository;

    public ScreeningService(ScreeningRepository screeningRepository) {
        this.screeningRepository = screeningRepository;
    }
    public List<ScreeningResponse> getScreeningsByMovieId(Long movieId) {
        List<Screening> screenings = screeningRepository.findByMovieId(movieId);
        return screenings.stream()
                .map(ScreeningResponse::from)
                .toList();
    }
}

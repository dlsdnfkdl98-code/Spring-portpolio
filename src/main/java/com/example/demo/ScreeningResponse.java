package com.example.demo;

import java.time.LocalDateTime;


public record ScreeningResponse(Long id, String screenName, LocalDateTime startTime) {
    public static ScreeningResponse from(Screening screening) {
        return new ScreeningResponse(screening.getId(), screening.getScreen().getName(), screening.getStartTime());
    }
}

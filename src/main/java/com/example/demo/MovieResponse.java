package com.example.demo;

public record MovieResponse(Long id, String title, int runningTime) {
    public static MovieResponse from(Movie movie) {
        return new MovieResponse(movie.getId(), movie.getTitle(), movie.getRunningTime());
    }
}

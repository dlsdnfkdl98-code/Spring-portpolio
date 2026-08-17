package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Movie {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String title;
	private int runningTime;

	protected Movie() {}

	public Movie(String title, int runningTime) {
		this.title = title;
		this.runningTime = runningTime;
	}

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getRunningTime() {
        return runningTime;
    }

}

package com.example.demo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;


@Entity

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    private Screen screen;

    private String seatRow;
    private int seatColumn;

    protected Seat() {}

    public Seat(Screen screen, String seatRow, int seatColumn) {
        this.screen = screen;
        this.seatRow = seatRow;
        this.seatColumn = seatColumn;
    }

    public Long getId() {
        return id;
    }

    public Screen getScreen() {
        return screen;
    }

    public String getRow() {
        return seatRow;
    }

    public int getColumn() {
        return seatColumn;
    }

}

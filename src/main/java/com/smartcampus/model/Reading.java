package com.smartcampus.model;

import java.time.LocalDateTime;

public class Reading {
    private double value;
    private LocalDateTime timestamp;

    public Reading() {
    }

    public Reading(double value, LocalDateTime timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

package com.smartcampus.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Sensor {
    private String id;
    private String type;
    private String description;
    // ACTIVE or MAINTENANCE
    private String status;
    private Double currentValue;
    private List<Reading> readings;

    public Sensor() {
        this.readings = new CopyOnWriteArrayList<>();
    }

    public Sensor(String id, String type, String description, String status, Double currentValue) {
        this.id = id;
        this.type = type;
        this.description = description;
        this.status = status;
        this.currentValue = currentValue;
        this.readings = new CopyOnWriteArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(Double currentValue) {
        this.currentValue = currentValue;
    }

    public List<Reading> getReadings() {
        return readings;
    }

    public void setReadings(List<Reading> readings) {
        this.readings = readings;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

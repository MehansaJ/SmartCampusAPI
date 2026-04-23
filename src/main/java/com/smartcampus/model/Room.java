package com.smartcampus.model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Room {
    private String id;
    private String name;
    private int floor;
    private List<Sensor> sensors;

    public Room() {
        this.sensors = new CopyOnWriteArrayList<>();
    }

    public Room(String id, String name, int floor) {
        this.id = id;
        this.name = name;
        this.floor = floor;
        this.sensors = new CopyOnWriteArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }
}
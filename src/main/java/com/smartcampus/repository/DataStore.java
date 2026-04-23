package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.Reading;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

// Singleton data store — holds all rooms and sensors in memory
public class DataStore {

    private static DataStore instance;

    private ConcurrentHashMap<String, Room> rooms;
    private ConcurrentHashMap<String, Sensor> sensors;
    private ConcurrentHashMap<String, List<Reading>> readings;

    private DataStore() {
        rooms = new ConcurrentHashMap<>();
        sensors = new ConcurrentHashMap<>();
        readings = new ConcurrentHashMap<>();
        initData();
    }

    // Returns the same instance every time
    public static synchronized DataStore getInstance() {
        if (instance == null) {
            instance = new DataStore();
        }
        return instance;
    }

    public ConcurrentHashMap<String, Room> getRooms() {
        return rooms;
    }

    public ConcurrentHashMap<String, Sensor> getSensors() {
        return sensors;
    }

    public ConcurrentHashMap<String, List<Reading>> getReadings() {
        return readings;
    }

    public void initData() {
        Room r1 = new Room("R001", "Library", 50);
        Room r2 = new Room("R002", "Lab 101", 30);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
    }
}

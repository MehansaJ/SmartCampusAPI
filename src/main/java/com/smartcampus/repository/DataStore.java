package com.smartcampus.repository;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;

import java.util.concurrent.ConcurrentHashMap;

// Singleton data store — holds all rooms and sensors in memory
public class DataStore {

    private static DataStore instance;

    private ConcurrentHashMap<String, Room> rooms;
    private ConcurrentHashMap<String, Sensor> sensors;

    private DataStore() {
        rooms = new ConcurrentHashMap<>();
        sensors = new ConcurrentHashMap<>();
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

    // Loads some default rooms so the API isn't empty on startup
    public void initData() {
        Room r1 = new Room("R001", "Library", 1);
        Room r2 = new Room("R002", "Lab 101", 2);

        rooms.put(r1.getId(), r1);
        rooms.put(r2.getId(), r2);
    }
}

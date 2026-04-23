package com.smartcampus.resources;

import com.smartcampus.model.Reading;
import com.smartcampus.model.Sensor;
import com.smartcampus.repository.DataStore;
import com.smartcampus.exceptions.SensorUnavailableException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReadingResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getReadings(@PathParam("id") String sensorId) {
        if (!dataStore.getSensors().containsKey(sensorId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor with ID '" + sensorId + "' not found.")
                    .build();
        }

        List<Reading> list = dataStore.getReadings().getOrDefault(sensorId, new ArrayList<>());
        return Response.ok(list).build();
    }

    @POST
    public Response addReading(@PathParam("id") String sensorId, Reading reading) {
        Sensor sensor = dataStore.getSensors().get(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Sensor with ID '" + sensorId + "' not found.")
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor '" + sensorId + "' is currently under MAINTENANCE and cannot accept new readings.");
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Reading data is missing.")
                    .build();
        }

        // Initialize readings list for sensor if missing
        dataStore.getReadings().putIfAbsent(sensorId, new CopyOnWriteArrayList<>());
        dataStore.getReadings().get(sensorId).add(reading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED).entity(reading).build();
    }
}

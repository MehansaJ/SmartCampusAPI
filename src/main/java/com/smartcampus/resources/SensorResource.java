package com.smartcampus.resources;

import com.smartcampus.model.Sensor;
import com.smartcampus.repository.DataStore;
import com.smartcampus.exceptions.LinkedResourceNotFoundException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Response getSensors(@QueryParam("type") String type) {
        Collection<Sensor> allSensors = dataStore.getSensors().values();

        // Return all sensors if no type filter is given
        if (type == null || type.trim().isEmpty()) {
            return Response.ok(allSensors).build();
        }

        List<Sensor> filteredSensors = new ArrayList<>();
        for (Sensor s : allSensors) {
            if (type.equalsIgnoreCase(s.getType())) {
                filteredSensors.add(s);
            }
        }

        return Response.ok(filteredSensors).build();
    }

    @POST
    public Response createSensor(@QueryParam("roomId") String roomId, Sensor sensor) {
        // 1. Check if roomId exists in the DataStore first
        if (roomId == null || roomId.trim().isEmpty() || !dataStore.getRooms().containsKey(roomId)) {
            throw new LinkedResourceNotFoundException("Room with ID '" + roomId + "' not found. Cannot link sensor.");
        }

        // 2. Validate sensor input
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Invalid input. Sensor ID is required.")
                           .build();
        }

        if (dataStore.getSensors().containsKey(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Sensor with ID '" + sensor.getId() + "' already exists in global store.")
                           .build();
        }

        // 3. Save the sensor
        sensor.setRoomId(roomId);
        dataStore.getSensors().put(sensor.getId(), sensor);

        // 4. Call helper method to link sensor to room
        dataStore.linkSensorToRoom(roomId, sensor.getId());

        return Response.status(Response.Status.CREATED).entity(sensor).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateSensor(@PathParam("id") String id, Sensor updatedSensorData) {
        if (!dataStore.getSensors().containsKey(id)) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Sensor with ID '" + id + "' not found to update.")
                           .build();
        }

        Sensor existingSensor = dataStore.getSensors().get(id);

        if (updatedSensorData != null) {
            // Update the current value field if provided
            if (updatedSensorData.getCurrentValue() != 0) {
                 existingSensor.setCurrentValue(updatedSensorData.getCurrentValue());
            }
            
            // Task 5.3: Update status field to allow demonstrating 403 Forbidden
            if (updatedSensorData.getStatus() != null && !updatedSensorData.getStatus().trim().isEmpty()) {
                existingSensor.setStatus(updatedSensorData.getStatus());
            }
        }

        return Response.ok(existingSensor).build();
    }

    // Task 4.1: Sub-Resource Locator Pattern
    @Path("/{id}/readings")
    public ReadingResource getReadingResource() {
        return new ReadingResource();
    }
}

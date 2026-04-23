package com.smartcampus.resources;

import com.smartcampus.model.Room;
import com.smartcampus.repository.DataStore;
import com.smartcampus.exceptions.RoomNotEmptyException;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.Collection;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore dataStore = DataStore.getInstance();

    @GET
    public Collection<Room> getAllRooms() {
        return dataStore.getRooms().values();
    }

    @GET
    @Path("/{id}")
    public Response getRoomById(@PathParam("id") String id) {
        Room room = dataStore.getRooms().get(id);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room with ID '" + id + "' not found.")
                           .build();
        }

        return Response.ok(room).build();
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                           .entity("Invalid Input. Room ID cannot be null or empty.")
                           .build();
        }

        // Reject if a room with the same ID already exists
        if (dataStore.getRooms().containsKey(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                           .entity("Room with ID '" + room.getId() + "' already exists.")
                           .build();
        }

        dataStore.getRooms().put(room.getId(), room);
        return Response.status(Response.Status.CREATED).entity(room).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = dataStore.getRooms().get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Room with ID '" + roomId + "' not found.")
                           .build();
        }

        // Can't delete a room that still has sensors in it
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Cannot delete room: Room '" + roomId + "' contains sensors.");
        }

        dataStore.getRooms().remove(roomId);
        return Response.noContent().build();
    }
}

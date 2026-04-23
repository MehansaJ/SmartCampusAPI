package com.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

// Root endpoint — shows basic API info and available links
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getDiscoveryInfo() {
        Map<String, Object> discoveryMap = new LinkedHashMap<>();
        discoveryMap.put("name", "Smart Campus API");
        discoveryMap.put("version", "1.0.0");
        discoveryMap.put("description", "RESTful API for Smart Campus Room and Sensor management.");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");

        discoveryMap.put("_links", links);

        return Response.ok(discoveryMap).build();
    }
}

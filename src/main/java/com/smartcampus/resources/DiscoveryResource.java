package com.smartcampus.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.LinkedHashMap;
import java.util.Map;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response getDiscoveryInfo() {
        Map<String, Object> discoveryMap = new LinkedHashMap<>();
        discoveryMap.put("name", "Smart Campus API");
        discoveryMap.put("version", "1.0.0");

        Map<String, String> contact = new LinkedHashMap<>();
        contact.put("author", "Jayathmi Mehansa Gunawardhana");
        contact.put("student_id", "20231209 / w2120249");
        contact.put("email", "w2120249@westminster.ac.uk");
        discoveryMap.put("contact", contact);

        discoveryMap.put("description", "RESTful API for Smart Campus Room and Sensor management.");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");

        discoveryMap.put("_links", links);

        return Response.ok(discoveryMap).build();
    }
}

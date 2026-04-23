package com.smartcampus;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class Main {

    // The server runs on port 8080
    public static final String BASE_URI = "http://localhost:8080/";

    public static void main(String[] args) {
        try {
            final SmartCampusApp config = new SmartCampusApp();

            // Start the embedded Grizzly server
            final HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), config);

            System.out.println("=========================================================");
            System.out.println("SmartCampusAPI Embedded Grizzly Server has started!");
            System.out.println("=========================================================");
            System.out.println("You can view your initialized standard discovery endpoint here:");
            System.out.println("👉 http://localhost:8080/api/v1");
            System.out.println("=========================================================");
            System.out.println("Leave this terminal open to keep the server running.");
            System.out.println("Hit Enter to safely stop the server...");

            System.in.read();
            server.shutdownNow();
        } catch (IOException ex) {
            System.err.println("Failed to start the Grizzly server: " + ex.getMessage());
        }
    }
}

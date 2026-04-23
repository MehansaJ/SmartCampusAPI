package com.smartcampus;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

// All API endpoints are under /api/v1
@ApplicationPath("/api/v1")
public class SmartCampusApp extends ResourceConfig {

    public SmartCampusApp() {
        packages("com.smartcampus.resources", "com.smartcampus.exceptions", "com.smartcampus.filters");
        register(JacksonFeature.class);
        register(ObjectMapperContextResolver.class);
    }

    // Configures Jackson to format dates as readable strings instead of numbers
    @Provider
    public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;

        public ObjectMapperContextResolver() {
            this.mapper = new ObjectMapper();
            this.mapper.registerModule(new JavaTimeModule());
            this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }
}
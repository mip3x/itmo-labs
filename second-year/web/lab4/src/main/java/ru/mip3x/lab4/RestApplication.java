package ru.mip3x.lab4;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

/**
 * Entry point for the RESTful web application
 * <p>
 * This class sets the base URI path ("/api") for all REST resources
 * using the {@link ApplicationPath} annotation
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
}

package ru.mip3x.lab4.endpoints;

import jakarta.inject.Singleton;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.mip3x.lab4.dto.UserDTO;
import ru.mip3x.lab4.service.AuthService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Singleton
public class AuthResource {
    private final AuthService authService = new AuthService();

    @POST
    @Path("/register")
    public Response register(UserDTO userDTO) {
        try {
            String sessionId = authService.register(userDTO);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"User registered successfully\", \"sessionId\":\"" + sessionId + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/login")
    public Response login(UserDTO userDTO) {
        try {
            String sessionId = authService.login(userDTO.getUsername(), userDTO.getPassword());
            return Response.ok("{\"message\":\"Login successful\", \"sessionId\":\"" + sessionId + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }

    @POST
    @Path("/logout")
    @Consumes("*/*")
    public Response logout(@HeaderParam("Authorization") String sessionId) {
        System.out.println("sessionId: " + sessionId);
        if (sessionId == null || authService.getUsernameFromSession(sessionId) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid session or not logged in\"}")
                    .build();
        }

        authService.logout(sessionId);
        return Response.ok("{\"message\":\"Logout successful\"}").build();
    }

    @GET
    @Path("/session")
    public Response getSession(@HeaderParam("Authorization") String sessionId) {
        String username = authService.getUsernameFromSession(sessionId);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid session or not logged in\"}")
                    .build();
        }

        return Response.ok("{\"username\":\"" + username + "\"}").build();
    }
}

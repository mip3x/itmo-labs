package ru.mip3x.lab4.endpoints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.mip3x.lab4.dto.UserDTO;
import ru.mip3x.lab4.service.AuthService;

/**
 * REST endpoint for user authentication and session management
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class AuthResource {
    @Inject
    private AuthService authService;

    /**
     * Registers a new user
     *
     * @param userDTO the user credentials
     * @return a response with a new session ID if successful; conflict error otherwise
     */
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

    /**
     * Logs in a user with provided credentials
     *
     * @param userDTO the user credentials
     * @return a response with a session ID if login is successful; unauthorized error otherwise
     */
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

    /**
     * Logs out the user associated with the given session ID
     *
     * @param sessionId the session ID from the Authorization header
     * @return a success response if logout is valid; unauthorized otherwise
     */
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

    /**
     * Retrieves the username associated with the session ID
     *
     * @param sessionId the session ID from the Authorization header
     * @return the username if session is valid; unauthorized otherwise
     */
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

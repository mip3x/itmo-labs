package ru.mip3x.lab4.endpoints;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.mip3x.lab4.dto.UserDTO;
import ru.mip3x.lab4.service.AuthService;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {
    private final AuthService authService = new AuthService();

    @POST
    @Path("/register")
    public Response register(UserDTO userDTO) {
        try {
            authService.register(userDTO);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\":\"User registered successfully\"}")
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
            UserDTO loggedInUser = authService.login(userDTO.getUsername(), userDTO.getPassword());
            return Response.ok("{\"message\":\"Login successful\", \"username\":\"" + loggedInUser.getUsername() + "\"}")
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}

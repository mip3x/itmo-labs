package ru.mip3x.lab4.endpoints;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import ru.mip3x.lab4.dto.UserDTO;

@Path("/auth")
public class AuthResource {
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(UserDTO user) {
        if ("user".equals(user.getUsername()) && "password".equals(user.getPassword())) {
            return Response.ok("{\"you\":\"guessed\"}").build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }
}
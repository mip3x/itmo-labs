package ru.mip3x.lab4.endpoints;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import ru.mip3x.lab4.dto.PointDTO;
import ru.mip3x.lab4.service.PointValidationService;

@Path("/plot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PlotResource {
    @Inject
    private PointValidationService validationService;

    @POST
    @Path("/check")
    public Response checkPoint(PointDTO pointDTO) {
        System.out.println("x: " + pointDTO.getX() + " y: " + pointDTO.getY() + " radius: " + pointDTO.getRadius());
        if (pointDTO.getX() == null || pointDTO.getY() == null || pointDTO.getRadius() == null || pointDTO.getRadius() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid input: x, y, and radius must be provided and radius must be positive\"}")
                    .build();
        }

        boolean result = validationService.isPointInArea(pointDTO.getX(), pointDTO.getY(), pointDTO.getRadius());
        return Response.ok("{\"result\": " + result + "}").build();
    }

}

package ru.mip3x.lab4.endpoints;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.inject.Inject;
import ru.mip3x.lab4.db.model.Result;
import ru.mip3x.lab4.db.model.User;
import ru.mip3x.lab4.dto.PointDTO;
import ru.mip3x.lab4.dto.ResultDTO;
import ru.mip3x.lab4.service.AuthService;
import ru.mip3x.lab4.service.PointValidationService;
import ru.mip3x.lab4.db.repository.ResultRepository;
import ru.mip3x.lab4.db.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST endpoint for validating and retrieving user-submitted points
 */
@Path("/plot")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class PlotResource {
    @Inject
    private PointValidationService validationService;

    @Inject
    private AuthService authService;

    private final UserRepository userRepository = new UserRepository();
    private final ResultRepository resultRepository = new ResultRepository();

    /**
     * Checks whether a point is inside a predefined area
     *
     * @param sessionId the session ID from the Authorization header
     * @param pointDTO  the coordinates and radius of the point
     * @return JSON with boolean result or an error response
     */
    @POST
    @Path("/check")
    @Consumes("*/*")
    public Response checkPoint(@HeaderParam("Authorization") String sessionId, PointDTO pointDTO) {
        String username = authService.getUsernameFromSession(sessionId);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid session or not logged in\"}")
                    .build();
        }

        if (pointDTO.getX() == null || pointDTO.getY() == null || pointDTO.getRadius() == null || pointDTO.getRadius() <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\":\"Invalid input: x, y, and radius must be provided and radius must be positive\"}")
                    .build();
        }

        boolean result = validationService.isPointInArea(pointDTO.getX(), pointDTO.getY(), pointDTO.getRadius());

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"User not found\"}")
                    .build();
        }

        Result newResult = new Result(
                pointDTO.getX(),
                pointDTO.getY(),
                pointDTO.getRadius(),
                result,
                LocalDateTime.now(),
                System.currentTimeMillis(),
                user
        );

        resultRepository.save(newResult);

        return Response.ok("{\"result\": " + result + "}").build();
    }

    /**
     * Returns all previously submitted points for the authenticated user
     *
     * @param sessionId the session ID from the Authorization header
     * @return list of submitted points or an error response
     */
    @GET
    @Path("/points")
    @Consumes("*/*")
    public Response getUserPoints(@HeaderParam("Authorization") String sessionId) {
        String username = authService.getUsernameFromSession(sessionId);
        if (username == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"Invalid session or not logged in\"}")
                    .build();
        }

        User user = userRepository.findByUsername(username);
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\":\"User not found\"}")
                    .build();
        }

        List<ResultDTO> resultDTOs = resultRepository.findResultsByUser(user).stream()
                .map(ResultDTO::new)
                .toList();
        for (int i = 0; i < resultDTOs.size(); i++) {
            ResultDTO resultDTO = resultDTOs.get(i);
            System.out.println("x " + resultDTO.getX() + " y " + resultDTO.getY() + " radius " + resultDTO.getR() + " result " + resultDTO.isResult());
        }

        return Response.ok(resultDTOs).build();
    }
}

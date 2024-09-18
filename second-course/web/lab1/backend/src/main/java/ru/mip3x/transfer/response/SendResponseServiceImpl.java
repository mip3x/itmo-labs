package ru.mip3x.transfer.response;

import ru.mip3x.dto.Response;
import ru.mip3x.server.Server;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

public class SendResponseServiceImpl implements SendResponseService {
    private final Logger logger;

    public SendResponseServiceImpl() {
        logger = Server.getLogger();
    }

    public void sendResponse(Response response) {
        logger.info("TRYING TO SEND RESPONSE...");

        String content = generateHtml(response);

        logger.info(content);
        String generatedResponse = """
                HTTP/2 %d %s
                Content-Type: text/html
                Content-Length: %d

                %s
                """.formatted(response.statusCode(), response.statusMessage(), content.getBytes(StandardCharsets.UTF_8).length, content);

        logger.info("RESPONSE SENT:\n" + generatedResponse);
        System.out.println(generatedResponse);
    }

    private String generateHtml(Response response) {
        if (response.validatedData() != null) {
            logger.info(response.statusMessage() + response.statusCode() + response.result());
            return """
                    <tr>
                    <td>%d</td>
                    <td>%.1f</td>
                    <td>%d</td>
                    <td>%b</td>
                    <td>%tS</td>
                    </tr>
                    """.formatted(
                    response.validatedData().x(), response.validatedData().y(), response.validatedData().radius(),
                    response.result(), System.nanoTime() -response.executionTime()
            );
        }
        String errorMessage = "Provided data is invalid";
        return """
                <td>%s</td>
                """.formatted(errorMessage);
    }
}

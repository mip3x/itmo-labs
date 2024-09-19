package ru.mip3x.transfer.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mip3x.dto.Response;

import java.nio.charset.StandardCharsets;

public class SendResponseServiceImpl implements SendResponseService {
    private final Logger logger = LogManager.getLogger(this.getClass());

    public void sendResponse(Response response, long executionStart) {
        logger.info("Trying to send response...");

        String content = generateHtml(response, executionStart);

        logger.info(content);
        String generatedResponse = """
                HTTP/2 %d %s
                Content-Type: text/html
                Content-Length: %d

                %s
                """.formatted(response.statusCode(), response.statusMessage(), content.getBytes(StandardCharsets.UTF_8).length, content);

        logger.info("Response sent: {}", generatedResponse);
        System.out.println(generatedResponse);
    }

    private String generateHtml(Response response, long executionStart) {
        if (response.data() == null) return "<td>Provided data is invalid</td>";

        return """
                    <tr>
                    <td>%d</td>
                    <td>%.1f</td>
                    <td>%d</td>
                    <td>%b</td>
                    <td>%tS</td>
                    </tr>
                    """.formatted(
                response.data().x(), response.data().y(), response.data().radius(),
                response.result(), System.nanoTime() - executionStart
        );
    }
}

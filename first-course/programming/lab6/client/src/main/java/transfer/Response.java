package transfer;

import java.io.Serializable;

public class Response implements Serializable {
    private String responseMessage;
    private ResponseStatus responseStatus;

    public Response() {
    }

    public Response(String text) {
        this.responseMessage = text;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public void setResponseMessage(String message) {
        this.responseMessage = message;
    }

    public void setResponseStatus(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }
}

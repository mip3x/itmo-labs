package transfer;

import validation.ValidationStatus;

import java.io.Serializable;

public class Response implements Serializable {
    private String responseMessage;
    private ValidationStatus responseStatus;
    private String responseStatusDescription;

    public Response() {
    }

    public Response(String text) {
        this.responseMessage = text;
    }

    public Response(String text, ValidationStatus validationStatus) {
        this.responseMessage = text;
        this.responseStatus = validationStatus;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public ValidationStatus getResponseStatus() {
        return responseStatus;
    }
    public String getResponseStatusDescription() {
        return responseStatusDescription;
    }

    public void setResponseMessage(String message) {
        this.responseMessage = message;
    }

    public void setResponseStatus(ValidationStatus validationStatus) {
        responseStatus = validationStatus;
    }

    public void setResponseStatusDescription(String responseStatusDescription) {
        this.responseStatusDescription = responseStatusDescription;
    }
}

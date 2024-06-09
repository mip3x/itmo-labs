package transfer;

import collection.data.StudyGroup;
import validation.ValidationStatus;

import java.io.Serializable;
import java.util.LinkedList;

public class Response implements Serializable {
    private String message;
    private ValidationStatus status;
    private String statusDescription;
    private LinkedList<StudyGroup> collection;

    public Response() {
    }

    public Response(String text) {
        this.message = text;
    }

    public Response(String text, ValidationStatus validationStatus) {
        this.message = text;
        this.status = validationStatus;
    }

    public Response(String text, ValidationStatus validationStatus, LinkedList<StudyGroup> collection) {
        this.message = text;
        this.status = validationStatus;
        this.collection = collection;
    }

    public Response(String text, ValidationStatus validationStatus, String statusDescription) {
        this.message = text;
        this.status = validationStatus;
        this.statusDescription = statusDescription;
    }

    public String getMessage() {
        return message;
    }

    public ValidationStatus getStatus() {
        return status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public LinkedList<StudyGroup> getCollection() {
        return collection;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(ValidationStatus validationStatus) {
        status = validationStatus;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setCollection(LinkedList<StudyGroup> collection) {
        this.collection = collection;
    }
}

package transfer;

import java.io.Serializable;

public class Response implements Serializable {
    private String response;

    public Response(String text) {
        this.response = text;
    }
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}

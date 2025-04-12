package identity.module.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value={"status", "statusText", "message"})
public class Response {

    private int status;

    private String statusText;

    private String message;

    public Response(int statusCode, String statusText, String message){
        this.status = statusCode;
        this.statusText = statusText;
        this.message = message;
    }

    @JsonGetter
    public int getStatus() {
        return status;
    }

    @JsonGetter
    public String getStatusText() {
        return statusText;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }
}

package identity.module.models;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value={"status", "error", "message"})
public class ErrorResponse {

    private int status;

    private String error;

    private String message;

    public ErrorResponse(int statusCode, String error, String message){
        this.status = statusCode;
        this.error = error;
        this.message = message;
    }

    @JsonGetter
    public int getStatus() {
        return status;
    }

    @JsonGetter
    public String getError() {
        return error;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }
}

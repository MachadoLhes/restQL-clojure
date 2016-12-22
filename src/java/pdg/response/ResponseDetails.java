package pdg.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Created by ideais on 15/12/16.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public class ResponseDetails {

    private int status;
    private boolean success;
    private String url;
    private int responseTime;
    private int timeout;

    public ResponseDetails() {
    }

    public static ResponseDetails fromJsonNode(JsonNode json) {
        ResponseDetails details = new ResponseDetails();
        details.setStatus(json.get("status").asInt());
        details.setSuccess(json.get("success").asBoolean());

        if (json.get("timeout") != null) {
            details.setTimeout(json.get("timeout").asInt());
        }

        if (json.get("url") != null) {
            details.setUrl(json.get("url").asText());
        }

        if (json.get("response-time") != null) {
            details.setResponseTime(json.get("response-time").asInt());
        }

        return details;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(int responseTime) {
        this.responseTime = responseTime;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}

package Result;

import Model.Event;

import java.util.List;

/**
 * Response for request to return ALL events for ALL family members of current user
 * So response to AllEvents Request class
 */
public class AllEventsResponse {
    /**
     * Store all of the data from Event class
     */
    private List<Event> data;
    /**
     * Message that output for if have error response (if the request was unsuccessful)
     */
    private String message;
    /**
     * bool that tells us if the request was sucessful (so if have success or error response to it)
     */
    private boolean success;

    /**
     * Response to getting all of the event class obj when have a successful response
     * @param data Store all of the event obj data in
     * @param success bool that tells us if the request was sucessful (so if have success or error response to it)
     */
    public AllEventsResponse(List<Event> data, boolean success) {
        this.data = data;
        this.success = success;
    }

    /**
     * Response to getting all of the event class obj when have a failed response
     * @param message Message that output for if have error response (if the request was unsuccessful)
     * @param success bool that tells us if the request was successful (so if have success or error response to it)
     */
    public AllEventsResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public List<Event> getEventData() {
        return data;
    }

    public void setEventData(List<Event> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

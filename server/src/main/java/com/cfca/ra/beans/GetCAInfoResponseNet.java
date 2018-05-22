package com.cfca.ra.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */

public class GetCAInfoResponseNet {
    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private final boolean success;

    /**
     * The name of the root CA associated with this server.
     */
    @SerializedName("Result")
    private GetCAInfoResponseResult result;

    /**
     * A array of error messages (i.e. code and string messages).
     */
    @SerializedName("Errors")
    private final List<ServerResponseError> errors;

    /**
     * A array of informational messages (i.e. code and string messages).
     */
    @SerializedName("Messages")
    private List<ServerResponseMessage> messages;

    public GetCAInfoResponseNet(final boolean success, final List<ServerResponseError> errors) {
        this.success = success;
        this.errors = errors;
    }

    public void setMessages(final List<ServerResponseMessage> messages) {
        this.messages = messages;
    }

    public void setResult(final GetCAInfoResponseResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public GetCAInfoResponseResult getResult() {
        return result;
    }

    public List<ServerResponseError> getErrors() {
        return errors;
    }

    public List<ServerResponseMessage> getMessages() {
        return messages;
    }

    @Override
    public String toString() {
        return "GetCAInfoResponseNet{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", errors=" + errors +
                ", messages=" + messages +
                '}';
    }
}

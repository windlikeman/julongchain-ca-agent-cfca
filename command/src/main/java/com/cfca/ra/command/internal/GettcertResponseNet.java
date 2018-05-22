package com.cfca.ra.command.internal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class GettcertResponseNet {


    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private final boolean success;

    /**
     * The name of the root CA associated with this server.
     */
    @SerializedName("Result")
    private GettcertResponseResult result;

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

    GettcertResponseNet(final boolean success, final List<ServerResponseError> errors) {
        this.success = success;
        this.errors = errors;
    }

    public void setMessages(final List<ServerResponseMessage> messages) {
        this.messages = messages;
    }

    public void setResult(final GettcertResponseResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public GettcertResponseResult getResult() {
        return result;
    }

    public List<ServerResponseError> getErrors() {
        return errors;
    }

    public List<ServerResponseMessage> getMessages() {
        return messages;
    }

}

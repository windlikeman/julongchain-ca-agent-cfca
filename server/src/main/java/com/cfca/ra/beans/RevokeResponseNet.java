package com.cfca.ra.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/14
 * @Description 吊销接口服务器返回给客户端的响应对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeResponseNet {
    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private boolean success;

    /**
     * Message related to revocation.
     */
    @SerializedName("Result")
    private String result;

    /**
     * A array of error messages (i.e. code and string messages).
     */
    @SerializedName("Errors")
    private List<ServerResponseError> errors;

    /**
     * A array of informational messages (i.e. code and string messages).
     */
    @SerializedName("Messages")
    private List<ServerResponseMessage> messages;

    public RevokeResponseNet(boolean success, String result, List<ServerResponseError> errors, List<ServerResponseMessage> messages) {
        this.success = success;
        this.result = result;
        this.errors = errors;
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getResult() {
        return result;
    }

    public List<ServerResponseError> getErrors() {
        return errors;
    }

    public List<ServerResponseMessage> getMessages() {
        return messages;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setErrors(List<ServerResponseError> errors) {
        this.errors = errors;
    }

    public void setMessages(List<ServerResponseMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "RevokeResponseNet{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", errors=" + errors +
                ", messages=" + messages +
                '}';
    }
}

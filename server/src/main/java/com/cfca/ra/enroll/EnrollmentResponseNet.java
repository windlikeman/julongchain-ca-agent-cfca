package com.cfca.ra.enroll;

import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.beans.ServerResponseMessage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description enroll接口网络响应请求参数,用于调用服务器Restful接口
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class EnrollmentResponseNet {
    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private boolean success;

    /**
     * The enrollment certificate in base 64 encoded format.
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

    public EnrollmentResponseNet(boolean success, String result, List<ServerResponseError> errors, List<ServerResponseMessage> messages) {
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

    public void setSuccess(final boolean success) {
        this.success = success;
    }

    public void setResult(final String result) {
        this.result = result;
    }

    public void setErrors(final List<ServerResponseError> errors) {
        this.errors = errors;
    }

    public void setMessages(final List<ServerResponseMessage> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "EnrollmentResponseNet{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", errors=" + errors +
                ", messages=" + messages +
                '}';
    }
}

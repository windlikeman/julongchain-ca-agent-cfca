package com.cfca.ra.register;

import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.beans.ServerResponseMessage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册接口服务器给客户端回复的对象
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RegistrationResponseNet {
    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private final boolean success;

    /**
     * The enrollment certificate in base 64 encoded format.
     */
    @SerializedName("Result")
    private final RegistrationResponseResult result;

    /**
     * A array of error messages (i.e. code and string messages).
     */
    @SerializedName("Errors")
    private final List<ServerResponseError> errors;

    /**
     * A array of informational messages (i.e. code and string messages).
     */
    @SerializedName("Messages")
    private final List<ServerResponseMessage> messages;

    public RegistrationResponseNet(boolean success, RegistrationResponseResult result, List<ServerResponseError> errors, List<ServerResponseMessage> messages) {
        this.success = success;
        this.result = result;
        this.errors = errors;
        this.messages = messages;
    }

    public boolean isSuccess() {
        return success;
    }

    public RegistrationResponseResult getResult() {
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
        return "EnrollmentResponse{" +
                "success=" + success +
                ", result='" + result + '\'' +
                ", errors=" + errors +
                ", messages=" + messages +
                '}';
    }
}

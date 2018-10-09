package org.bica.julongchain.cfca.ra.command.internal.revoke;

import com.google.gson.annotations.SerializedName;

import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseMessage;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用于接受吊销命令的服务器响应
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeResponseNet {
    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private final boolean success;

    /**
     * The enrollment certificate in base 64 encoded format.
     */
    @SerializedName("Result")
    private final String result;

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RevokeResponseNet [success=");
        builder.append(success);
        builder.append(", result=");
        builder.append(result);
        builder.append(", errors=");
        builder.append(errors);
        builder.append(", messages=");
        builder.append(messages);
        builder.append("]");
        return builder.toString();
    }

   
    
}

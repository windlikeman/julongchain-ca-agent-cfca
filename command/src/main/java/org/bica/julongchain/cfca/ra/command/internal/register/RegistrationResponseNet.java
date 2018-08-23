package org.bica.julongchain.cfca.ra.command.internal.register;

import com.google.gson.annotations.SerializedName;

import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseMessage;

import java.util.List;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册接口服务器给客户端回复的对象
 * @CodeReviewer
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
        StringBuilder builder = new StringBuilder();
        builder.append("RegistrationResponseNet [success=");
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

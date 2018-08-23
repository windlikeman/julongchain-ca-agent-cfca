package org.bica.julongchain.cfca.ra.command.internal.getcainfo;

import com.google.gson.annotations.SerializedName;

import org.bica.julongchain.cfca.ra.command.internal.ServerResponseError;
import org.bica.julongchain.cfca.ra.command.internal.ServerResponseMessage;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description GetCAInfo命令的网络响应对象
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
    private final GetCAInfoResponseResult result;

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

    GetCAInfoResponseNet(boolean success, GetCAInfoResponseResult result, List<ServerResponseError> errors, List<ServerResponseMessage> messages) {
        this.success = success;
        this.result = result;
        this.errors = errors;
        this.messages = messages;
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
        StringBuilder builder = new StringBuilder();
        builder.append("GetCAInfoResponseNet [success=");
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

package com.cfca.ra.gettcert;

import com.cfca.ra.beans.ServerResponseError;
import com.cfca.ra.beans.ServerResponseMessage;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description GettCert命令服务器返回给客户端的对象
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class GettCertResponseNet {


    /**
     * Boolean indicating if the request was successful.
     */
    @SerializedName("Success")
    private final boolean success;

    /**
     * The name of the root CA associated with this server.
     */
    @SerializedName("Result")
    private GettCertResponseResult result;

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

    public GettCertResponseNet(final boolean success, final List<ServerResponseError> errors) {
        this.success = success;
        this.errors = errors;
    }

    public void setMessages(final List<ServerResponseMessage> messages) {
        this.messages = messages;
    }

    public void setResult(final GettCertResponseResult result) {
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public GettCertResponseResult getResult() {
        return result;
    }

    public List<ServerResponseError> getErrors() {
        return errors;
    }

    public List<ServerResponseMessage> getMessages() {
        return messages;
    }

}

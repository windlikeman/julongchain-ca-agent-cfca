package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class ServerResponseMessage {
    public static final int RESPONSE_MESSAGE_CODE_CANAME = 0x101;
    public static final int RESPONSE_MESSAGE_CODE_VERSION = 0x102;
    public static final int RESPONSE_MESSAGE_CODE_CACHAIN = 0x103;

    /**
     * Integer code denoting the type of message.
     */
    private final int code;
    /**
     * A more specific message.
     */
    private final String message;

    public ServerResponseMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServerResponseMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

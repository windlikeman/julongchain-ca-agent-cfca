package com.cfca.ra.command.internal;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class ServerResponseMessage {
    static final int RESPONSE_MESSAGE_CODE_CANAME = 0x101;
    static final int RESPONSE_MESSAGE_CODE_VERSION = 0x102;
    static final int RESPONSE_MESSAGE_CODE_CACHAIN = 0x103;
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

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ServerResponseMessage{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

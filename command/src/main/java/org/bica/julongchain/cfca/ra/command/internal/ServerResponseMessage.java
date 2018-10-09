package org.bica.julongchain.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 服务器Restful接口返回给客户端的其他信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class ServerResponseMessage {
    static final int RESPONSE_MESSAGE_CODE_CANAME = 0x101;
    static final int RESPONSE_MESSAGE_CODE_VERSION = 0x102;
    static final int RESPONSE_MESSAGE_CODE_CACHAIN = 0x103;
    static final int RESPONSE_MESSAGE_CODE_ENROLLMENTID = 0x104;
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
        StringBuilder builder = new StringBuilder();
        builder.append("ServerResponseMessage [code=");
        builder.append(code);
        builder.append(", message=");
        builder.append(message);
        builder.append("]");
        return builder.toString();
    }

    
    
}

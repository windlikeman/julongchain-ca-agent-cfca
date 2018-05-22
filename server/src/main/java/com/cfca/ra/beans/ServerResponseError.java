package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 服务器Restful接口返回给客户端的错误信息
 * @CodeReviewer
 * @since v3.0.0
 */
public class ServerResponseError {
    /**
     * Integer code denoting the type of error.
     */
    private final int code;
    /**
     * An error message
     */
    private final String message;

    public ServerResponseError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ServerResponseError{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}

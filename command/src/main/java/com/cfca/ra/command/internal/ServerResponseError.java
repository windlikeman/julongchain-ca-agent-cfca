package com.cfca.ra.command.internal;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class ServerResponseError {
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

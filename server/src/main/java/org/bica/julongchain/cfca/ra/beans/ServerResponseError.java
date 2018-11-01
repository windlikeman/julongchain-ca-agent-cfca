package org.bica.julongchain.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 服务器Restful接口返回给客户端的错误信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class ServerResponseError {
    /**
     * An error message
     */
    private final String message;

    public ServerResponseError(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ServerResponseError [message=");
        builder.append(message);
        builder.append("]");
        return builder.toString();
    }
}

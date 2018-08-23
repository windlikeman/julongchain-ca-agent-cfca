package org.bica.julongchain.cfca.ra.command.internal.revoke;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description 吊销接口服务器返回给客户端的响应对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeResponse {

    private final String result;

    public RevokeResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RevokeResponse [result=");
        builder.append(result);
        builder.append("]");
        return builder.toString();
    }

   
    
}

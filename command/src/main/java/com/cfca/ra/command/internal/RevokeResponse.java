package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description 吊销接口服务器返回给客户端的响应对象
 * @CodeReviewer
 * @since v3.0.0
 */
class RevokeResponse {

    private final String result;

    RevokeResponse(String result) {
        this.result = result;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "RevokeResponse{" +
                "result='" + result + '\'' +
                '}';
    }
}

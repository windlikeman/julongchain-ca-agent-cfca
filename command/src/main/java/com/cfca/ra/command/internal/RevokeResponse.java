package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/21
 * @Description
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

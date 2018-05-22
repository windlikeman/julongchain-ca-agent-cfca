package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/17
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class GetCAInfoRequest {
    private final String caName;

    GetCAInfoRequest(String caName) {
        this.caName = caName;
    }

    public String getCaName() {
        return caName;
    }

    @Override
    public String toString() {
        return "GetCAInfoRequest{" +
                "caName='" + caName + '\'' +
                '}';
    }
}

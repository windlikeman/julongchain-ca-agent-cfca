package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 鉴权信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class AuthInfo {
    private final String user;
    private final String secret;
    public AuthInfo(final String user, final String secret) {
        this.user = user;
        this.secret = secret;
    }

    public String getUser() {
        return user;
    }

    public String getSecret() {
        return secret;
    }

    @Override
    public String toString() {
        return "AuthInfo{" +
                "user='" + user + '\'' +
                ", secret='hide'" +
                '}';
    }
}

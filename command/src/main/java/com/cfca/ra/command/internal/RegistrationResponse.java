package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class RegistrationResponse {
    private final String secret;

    RegistrationResponse(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

}

package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 命令行内部使用的注册接口响应对象
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

package com.cfca.ra.command.internal.register;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 命令行内部使用的注册接口响应对象
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationResponse {
    private final String secret;

    public RegistrationResponse(String secret) {
        this.secret = secret;
    }

    public String getSecret() {
        return secret;
    }

}

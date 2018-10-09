package org.bica.julongchain.cfca.ra.command.internal.register;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 注册接口服务器给客户端回复的对象的结果,包含注册成功的口令
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationResponseResult {
    private final String redentials;

    RegistrationResponseResult(String redentials) {
        this.redentials = redentials;
    }

    public String getRedentials() {
        return redentials;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegistrationResponseResult [redentials=");
        builder.append(redentials);
        builder.append("]");
        return builder.toString();
    }

    
    
}

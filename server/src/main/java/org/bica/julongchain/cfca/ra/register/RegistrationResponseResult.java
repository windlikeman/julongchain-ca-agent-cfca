package org.bica.julongchain.cfca.ra.register;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 注册接口服务器给客户端回复的对象的结果,包含注册成功的口令
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class RegistrationResponseResult {
    private final String redentials;

    public RegistrationResponseResult(String redentials) {
        this.redentials = redentials;
    }

    public String getRedentials() {
        return redentials;
    }

    @Override
    public String toString() {
        return "RegistrationResponseResult{" +
                "redentials='" + redentials + '\'' +
                '}';
    }
}
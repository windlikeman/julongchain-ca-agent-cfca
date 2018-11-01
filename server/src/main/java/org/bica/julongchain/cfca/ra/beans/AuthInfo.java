package org.bica.julongchain.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/16
 * @Description 鉴权信息
 * @CodeReviewer helonglong
 * @since v3.0.0
 */
public class AuthInfo {
    private final String seqNo;
    private final String user;
    private final String secret;
    public AuthInfo(final String user, final String seqNo, final String secret) {
        this.user = user;
        this.seqNo = seqNo;
        this.secret = secret;
    }

    public String getSeqNo() {
        return seqNo;
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
                ", seqNo='"+seqNo+"\'" +
                '}';
    }
}

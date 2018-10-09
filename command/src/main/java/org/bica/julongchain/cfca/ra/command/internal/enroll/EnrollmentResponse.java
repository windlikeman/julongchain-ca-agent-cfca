package org.bica.julongchain.cfca.ra.command.internal.enroll;


import org.bica.julongchain.cfca.ra.command.internal.Identity;
import org.bica.julongchain.cfca.ra.command.internal.ServerInfo;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 签发证书的命令行内部使用的响应对象,用于适配服务器返回的响应参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentResponse {
    private final Identity identity;
    private final ServerInfo serverInfo;

    public EnrollmentResponse(Identity identity, ServerInfo serverInfo) {
        this.identity = identity;
        this.serverInfo = serverInfo;
    }

    public Identity getIdentity() {
        return identity;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("EnrollmentResponse [identity=");
        builder.append(identity);
        builder.append(", serverInfo=");
        builder.append(serverInfo);
        builder.append("]");
        return builder.toString();
    }

   
}

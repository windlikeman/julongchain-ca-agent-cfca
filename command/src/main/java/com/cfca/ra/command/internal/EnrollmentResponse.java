package com.cfca.ra.command.internal;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
class EnrollmentResponse {
    private final Identity identity;
    private final ServerInfo serverInfo;

    EnrollmentResponse(Identity identity, ServerInfo serverInfo){
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
        return "EnrollmentResponse{" +
                "identity=" + identity +
                ", serverInfo=" + serverInfo +
                '}';
    }
}

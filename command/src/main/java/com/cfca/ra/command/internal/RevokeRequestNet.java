package com.cfca.ra.command.internal;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 吊销接口网络请求参数,用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeRequestNet {
    private final String id;

    private final String aki;

    private final String serial;

    private final String reason;

    private final String caname;

    public RevokeRequestNet(String id, String aki, String serial, String reason, String caname) {
        this.id = id;
        this.aki = aki;
        this.serial = serial;
        this.reason = reason;
        this.caname = caname;
    }

    RevokeRequestNet(RevokeRequest registrationRequest) {
        this.id = registrationRequest.getId();
        this.aki = registrationRequest.getAki();
        this.serial = registrationRequest.getSerial();
        this.reason = registrationRequest.getReason();
        this.caname = registrationRequest.getCaname();
    }

    public String getId() {
        return id;
    }

    public String getAki() {
        return aki;
    }

    public String getSerial() {
        return serial;
    }

    public String getReason() {
        return reason;
    }

    public String getCaname() {
        return caname;
    }
}

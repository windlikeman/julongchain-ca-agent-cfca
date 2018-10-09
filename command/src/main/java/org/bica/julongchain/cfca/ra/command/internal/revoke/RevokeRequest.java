package org.bica.julongchain.cfca.ra.command.internal.revoke;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令行内部使用的吊销请求,用于适配网络Restful接口请求
 * @CodeReviewer
 * @since v3.0.0
 */
public class RevokeRequest {
    private final String id;

    private final String aki;

    private final String serial;

    private final String reason;

    private final String caname;

    public RevokeRequest(String id, String aki, String serial, String reason, String caname) {
        this.id = id;
        this.aki = aki;
        this.serial = serial;
        this.reason = reason;
        this.caname = caname;
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RevokeRequest [id=");
        builder.append(id);
        builder.append(", aki=");
        builder.append(aki);
        builder.append(", serial=");
        builder.append(serial);
        builder.append(", reason=");
        builder.append(reason);
        builder.append(", caname=");
        builder.append(caname);
        builder.append("]");
        return builder.toString();
    }

    
}

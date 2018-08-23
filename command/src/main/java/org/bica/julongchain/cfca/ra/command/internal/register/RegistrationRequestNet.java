package org.bica.julongchain.cfca.ra.command.internal.register;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 注册接口网络请求参数,用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationRequestNet {
    /**
     * 用户id 不可空
     */
    private final String id;
    /**
     * 类型 不可空
     */
    private final String type;
    /**
     * 密码 不可空
     */
    private final String secret;

    /**
     * 最大证书数量 不可空
     */
    @SerializedName("max_enrollments")
    private final int maxEnrollments;

    /**
     * 路径 不可空
     */
    @SerializedName("affiliation_path")
    private final String affiliationPath;

    /**
     * 用数组传入多个属性值 不可空
     */
    private final List<UserAttrs> attrs;

    /**
     * ca名称 不可空
     */
    private final String caname;

    RegistrationRequestNet(String id, String type, String secret, int maxEnrollments, String affiliationPath, List<UserAttrs> attrs, String caname) {
        this.id = id;
        this.type = type;
        this.secret = secret;
        this.maxEnrollments = maxEnrollments;
        this.affiliationPath = affiliationPath;
        this.attrs = attrs;
        this.caname = caname;
    }

    public RegistrationRequestNet(RegistrationRequest registrationRequest) {
        this.id = registrationRequest.getName();
        this.type = registrationRequest.getType();
        this.secret = registrationRequest.getSecret();
        this.maxEnrollments = registrationRequest.getMaxEnrollments();
        this.affiliationPath = registrationRequest.getAffiliation();
        this.attrs = registrationRequest.getAttributes();
        this.caname = registrationRequest.getCaName();
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getSecret() {
        return secret;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public String getAffiliationPath() {
        return affiliationPath;
    }

    public List<UserAttrs> getAttrs() {
        return attrs;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RegistrationRequestNet [id=");
        builder.append(id);
        builder.append(", type=");
        builder.append(type);
        builder.append(", secret=");
        builder.append(secret);
        builder.append(", maxEnrollments=");
        builder.append(maxEnrollments);
        builder.append(", affiliationPath=");
        builder.append(affiliationPath);
        builder.append(", attrs=");
        builder.append(attrs);
        builder.append(", caname=");
        builder.append(caname);
        builder.append("]");
        return builder.toString();
    }

    public String getCaname() {
        return caname;
    }
    
    
}

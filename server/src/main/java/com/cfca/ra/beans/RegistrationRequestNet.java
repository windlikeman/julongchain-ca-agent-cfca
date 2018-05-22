package com.cfca.ra.beans;

import com.cfca.ra.ca.register.UserAttrs;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationRequestNet {
    /**
     * 用户id	不可空
     */
    private final String id;
    /**
     * 类型	不可空
     */
    private final String type;
    /**
     * 密码	不可空
     */
    private final String secret;

    /**
     * 最大证书数量	不可空
     */
    @SerializedName("max_enrollments")
    private final int maxEnrollments;

    /**
     * 路径	不可空
     */
    @SerializedName("affiliation_path")
    private final String affiliationPath;

    /**
     * 用数组传入多个属性值	不可空
     */
    private final List<UserAttrs> attrs;

    /**
     * ca名称	不可空
     */
    private final String caname;

    public RegistrationRequestNet(String id, String type, String secret, int maxEnrollments, String affiliationPath, List<UserAttrs> attrs, String caname) {
        this.id = id;
        this.type = type;
        this.secret = secret;
        this.maxEnrollments = maxEnrollments;
        this.affiliationPath = affiliationPath;
        this.attrs = attrs;
        this.caname = caname;
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

    public String getCaname() {
        return caname;
    }

    @Override
    public String toString() {
        return "RegistrationRequestNet{" +
                "id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", secret='" + secret + '\'' +
                ", maxEnrollments=" + maxEnrollments +
                ", affiliationPath='" + affiliationPath + '\'' +
                ", attrs=" + attrs +
                ", caname='" + caname + '\'' +
                '}';
    }
}

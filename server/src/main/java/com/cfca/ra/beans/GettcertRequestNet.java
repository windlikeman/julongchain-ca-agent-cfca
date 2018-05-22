package com.cfca.ra.beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description GettCert命令的网络请求对象,用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertRequestNet {
    /**
     * 查询证书数目
     */
    private final int count;

    /**
     * 查询属性
     */
    @SerializedName("attr_names")
    private final List<String> attrNames;

    /**
     * 是否加密
     */
    @SerializedName("encrypt_attrs")
    private final boolean encryptAttrs;

    /**
     * 有效期	不可空
     */
    @SerializedName("validity_period")
    private final int validityPeriod;

    /**
     * ca名称
     */
    private final String caname;

    public GettCertRequestNet(int count, List<String> attrNames, boolean encryptAttrs, int validityPeriod, String caname) {
        this.count = count;
        this.attrNames = attrNames;
        this.encryptAttrs = encryptAttrs;
        this.validityPeriod = validityPeriod;
        this.caname = caname;
    }

    public int getCount() {
        return count;
    }

    public List<String> getAttrNames() {
        return attrNames;
    }

    public boolean isEncryptAttrs() {
        return encryptAttrs;
    }

    public int getValidityPeriod() {
        return validityPeriod;
    }

    public String getCaname() {
        return caname;
    }
}

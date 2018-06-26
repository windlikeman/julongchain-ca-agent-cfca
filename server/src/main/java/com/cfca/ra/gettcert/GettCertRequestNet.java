package com.cfca.ra.gettcert;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description GettCert命令的网络请求对象,用于调用服务器Restful接口
 * @CodeReviewer helonglong
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GettCertRequestNet that = (GettCertRequestNet) o;
        return count == that.count &&
                encryptAttrs == that.encryptAttrs &&
                validityPeriod == that.validityPeriod &&
                Objects.equals(attrNames, that.attrNames) &&
                Objects.equals(caname, that.caname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, attrNames, encryptAttrs, validityPeriod, caname);
    }

    @Override
    public String toString() {
        return "GettCertRequestNet{" +
                "count=" + count +
                ", attrNames=" + attrNames +
                ", encryptAttrs=" + encryptAttrs +
                ", validityPeriod=" + validityPeriod +
                ", caname='" + caname + '\'' +
                '}';
    }
}

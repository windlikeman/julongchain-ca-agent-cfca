package com.cfca.ra.command.internal.gettcert;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令行内部使用的GettCert 命令请求,用于适配网络Restful接口请求
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertRequest {
    /**
     * 查询证书数目
     */
    private final int count;

    /**
     * 查询属性
     */
    private final List<String> attrNames;

    /**
     * 是否加密
     */
    private final boolean encryptAttrs;

    /**
     * 有效期
     */
    private final int validityPeriod;

    /**
     * ca名称
     */
    private final String caname;

    public GettCertRequest(int count, List<String> attrNames, boolean encryptAttrs, int validityPeriod, String caname) {
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
    public String toString() {
        return "GettCertRequest{" +
                "count=" + count +
                ", attrNames=" + attrNames +
                ", encryptAttrs=" + encryptAttrs +
                ", validityPeriod=" + validityPeriod +
                ", caname='" + caname + '\'' +
                '}';
    }
}

package com.cfca.ra.gettcert;

import com.cfca.ra.beans.BaseRequest;
import com.cfca.ra.ca.Attribute;
import com.cfca.ra.ca.TcertManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 命令行内部使用的GettCert 命令请求,用于适配网络Restful接口请求
 * @CodeReviewer
 * @since v3.0.0
 */
public class GettCertRequest extends BaseRequest{
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
     * 有效期,以小时为单位
     */
    private final long validityPeriod;

    /**
     * ca名称
     */
    private final String caname;

    /**
     * 用于密钥派生的前置密钥
     */
    private final String preKey;

    /**
     * DisableKeyDerivation 如果为 true,则禁用密钥派生,以使TCert与ECert密码不相关.
     * 当使用不支持TCert密钥派生函数的 HSM 时,这可能是必需的.
     */
    private final boolean disableKeyDerivation;

    private final List<Attribute> attrs;

    private final long timestamp;

    private GettCertRequest(Builder builder) {
        this.count = builder.count;
        this.attrNames = builder.attrNames;
        this.encryptAttrs = builder.encryptAttrs;
        this.validityPeriod = builder.validityPeriod;
        this.caname = builder.caname;
        this.preKey = builder.preKey;
        this.disableKeyDerivation = builder.disableKeyDerivation;
        this.attrs = builder.attrs;
        this.timestamp = builder.timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getPreKey() {
        return preKey;
    }

    public boolean isDisableKeyDerivation() {
        return disableKeyDerivation;
    }

    public List<Attribute> getAttrs() {
        return attrs;
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

    public long getValidityPeriod() {
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
        GettCertRequest that = (GettCertRequest) o;
        return count == that.count &&
                encryptAttrs == that.encryptAttrs &&
                validityPeriod == that.validityPeriod &&
                disableKeyDerivation == that.disableKeyDerivation &&
                timestamp == that.timestamp &&
                Objects.equals(attrNames, that.attrNames) &&
                Objects.equals(caname, that.caname) &&
                Objects.equals(preKey, that.preKey) &&
                Objects.equals(attrs, that.attrs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, attrNames, encryptAttrs, validityPeriod, caname, preKey, disableKeyDerivation, attrs, timestamp);
    }

    @Override
    public String toString() {
        return "GettCertRequest{" +
                "count=" + count +
                ", attrNames=" + attrNames +
                ", encryptAttrs=" + encryptAttrs +
                ", validityPeriod=" + validityPeriod +
                ", caname='" + caname + '\'' +
                ", preKey='" + preKey + '\'' +
                ", disableKeyDerivation=" + disableKeyDerivation +
                ", attrs=" + attrs +
                ", timestamp=" + timestamp +
                '}';
    }

    public static class Builder {
        private final boolean encryptAttrs;
        private final String caname;
        private final String preKey;
        private final List<Attribute> attrs;
        private final long timestamp;

        private int count = 1000;
        private long validityPeriod = TcertManager.HOUR * 24 * 365;
        private boolean disableKeyDerivation = true;
        private List<String> attrNames = new ArrayList<>();

        public Builder(List<Attribute> attrs, boolean encryptAttrs, String caname, int count, String preKey) {
            this.attrs = attrs;
            this.encryptAttrs = encryptAttrs;
            this.caname = caname;
            this.count = count;
            this.preKey = preKey;
            this.timestamp = System.currentTimeMillis();
        }

        Builder count(int v) {
            this.count = v;
            return this;
        }

        Builder validityPeriod(long v) {
            this.validityPeriod = v * TcertManager.HOUR;
            return this;
        }

        Builder attrNames(List<String> v) {
            this.attrNames = v;
            return this;
        }

        public GettCertRequest build() {
            return new GettCertRequest(this);
        }
    }
}

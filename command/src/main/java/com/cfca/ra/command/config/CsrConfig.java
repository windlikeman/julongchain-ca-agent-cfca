package com.cfca.ra.command.config;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description 证书签名申请部分，用于生成注册证书的CSR(ECert)
 * @CodeReviewer
 * @since v3.0.0
 */
public class CsrConfig {
    /**
     * 由CA用来确定要生成证书的common name
     */
    private String cn;

    /**
     * 序列号
     */
    private String serialnumber;

    /**
     * 证书的一组name. 至少包含下列一个的值:
     *   "C", "L", "O", or "ST" 他们代表:
     *       "C": country
     *       "L": locality or municipality (such as city or town name)
     *       "O": organization
     *       "OU": organizational unit, such as the department responsible for owning the key;
     *       it can also be used for a "Doing Business As" (DBS) name
     *       "ST": the state or province
     *
     *  请注意,ECert的"OU"或组织单位总是根据身份类型和隶属关系的值进行设置
     *  OU的计算方式为 OU = <type>, OU = <affiliationRoot>,...,OU = <affiliationLeaf>
     *  例如,具有"org1.dept2.team3"属性的"client"类型的标识将具有以下组织单位:
     *  OU = client,OU = org1,OU = dept2,OU = team3
     */
    private String names;

    /**
     * 证书应该有效的主机名列表
     */
    private List<String> hosts;

    /**
     * 加密算法和密钥长度
     */
    private KeyConfig key;

    /**
     * CA 配置信息
     */
    private CAConfig ca;

    public void setKey(final KeyConfig key) {
        this.key = key;
    }

    public void setCn(final String cn) {
        this.cn = cn;
    }

    public void setSerialnumber(final String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public void setNames(final String names) {
        this.names = names;
    }

    public void setHosts(final List<String> hosts) {
        this.hosts = hosts;
    }

    public String getCn() {
        return cn;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public String getNames() {
        return names;
    }

    public List<String> getHosts() {
        return hosts;
    }

    public KeyConfig getKey() {
        return key;
    }

    public CAConfig getCa() {
        return ca;
    }

    public void setCa(final CAConfig ca) {
        this.ca = ca;
    }

    @Override
    public String toString() {
        return "CsrConfig{" +
                "cn='" + cn + '\'' +
                ", serialnumber='" + serialnumber + '\'' +
                ", names=" + names +
                ", hosts=" + hosts +
                ", key=" + key +
                ", ca=" + ca +
                '}';
    }
}

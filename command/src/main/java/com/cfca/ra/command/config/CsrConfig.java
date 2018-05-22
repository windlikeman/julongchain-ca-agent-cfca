package com.cfca.ra.command.config;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/15
 * @Description Certificate Signing Request section for generating the CSR for an enrollment certificate (ECert)
 * @CodeReviewer
 * @since v3.0.0
 */
public class CsrConfig {
    /**
     * Used by CAs to determine which domain the certificate is to be generated for
     */
    private String cn;

    /**
     * The serialnumber field, if specified, becomes part of the issued
     * certificate's DN (Distinguished Name).  For example, one use case for this is
     * a company with its own CA (Certificate Authority) which issues certificates
     * to its employees and wants to include the employee's serial number in the DN
     * of its issued certificates.
     * WARNING: The serialnumber field should not be confused with the certificate's
     * serial number which is set by the CA but is not a component of the
     * certificate's DN.
     */
    private String serialnumber;

    /**
     *  A list of name objects. Each name object should contain at least one
     *    "C", "L", "O", or "ST" value (or any combination of these) where these
     *    are abbreviations for the following:
     *        "C": country
     *        "L": locality or municipality (such as city or town name)
     *        "O": organization
     *        "OU": organizational unit, such as the department responsible for owning the key;
     *         it can also be used for a "Doing Business As" (DBS) name
     *        "ST": the state or province
     *
     *    Note that the "OU" or organizational units of an ECert are always set according
     *    to the values of the identities type and affiliation. OUs are calculated for an enroll
     *    as OU=<type>, OU=<affiliationRoot>, ..., OU=<affiliationLeaf>. For example, an identity
     *    of type "client" with an affiliation of "org1.dept2.team3" would have the following
     *    organizational units: OU=client, OU=org1, OU=dept2, OU=team3
     */
//    private List<NameObject> names;
    private String names;
    /**
     * A list of host names for which the certificate should be valid
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

    public void setKey(KeyConfig key) {
        this.key = key;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public String getCn() {
        return cn;
    }

    public String getSerialnumber() {
        return serialnumber;
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

    public void setCa(CAConfig ca) {
        this.ca = ca;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
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

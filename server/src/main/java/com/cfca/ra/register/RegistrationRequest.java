package com.cfca.ra.register;

import com.cfca.ra.ca.Attribute;

import java.util.List;
import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description 服务器内部使用注册接口参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class RegistrationRequest {
    /**
     * 消息时间戳,单位毫秒
     */
    private final long timestamp;
    /**
     * MaxEnrollments is the maximum number of times the secret can be reused to enroll.
     */
    private int maxEnrollments;

    /**
     * Name is the unique name of the identity
     */
    private String name;

    /**
     * Type of identity being registered (e.g. "peer, app, user")
     */
    private String type;

    /**
     * Secret is an optional password.  If not specified,a random secret is generated.  In both cases, the secret is returned in the RegistrationResponse.
     */
    private String secret;

    /**
     * is returned in the response.
     * The identity's affiliation.
     * For example, an affiliation of "org1.department1" associates the identity with "department1" in "org1".
     */
    private String affiliation ;

    /**
     * Attributes associated with this identity
     */
    private List<Attribute> attribute;

    /**
     * CAName is the name of the CA to connect to
     */
    private String caName;

    public RegistrationRequest(RegistrationRequestNet registrationRequestNet) {
        this.caName = registrationRequestNet.getCaname();
        this.name = registrationRequestNet.getId();
        this.affiliation = registrationRequestNet.getAffiliationPath();
        this.attribute = registrationRequestNet.getAttrs();
        this.secret = registrationRequestNet.getSecret();
        this.type = registrationRequestNet.getType();
        this.maxEnrollments = registrationRequestNet.getMaxEnrollments();
        this.timestamp = System.currentTimeMillis();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getMaxEnrollments() {
        return maxEnrollments;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public void setAffiliation(String affiliation) {
        this.affiliation = affiliation;
    }

    public List<Attribute> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<Attribute> attribute) {
        this.attribute = attribute;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public void setMaxEnrollments(int maxEnrollments) {
        this.maxEnrollments = maxEnrollments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RegistrationRequest that = (RegistrationRequest) o;
        return timestamp == that.timestamp &&
                maxEnrollments == that.maxEnrollments &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Objects.equals(secret, that.secret) &&
                Objects.equals(affiliation, that.affiliation) &&
                Objects.equals(attribute, that.attribute) &&
                Objects.equals(caName, that.caName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(timestamp, maxEnrollments, name, type, secret, affiliation, attribute, caName);
    }

    @Override
    public String toString() {
        return "RegistrationRequest{" +
                "timestamp=" + timestamp +
                ", maxEnrollments=" + maxEnrollments +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", secret='hide" + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", attribute=" + attribute +
                ", caName='" + caName + '\'' +
                '}';
    }


}

package com.cfca.ra.beans;

import com.cfca.ra.ca.register.UserAttrs;

import java.util.List;

/**
 * @author zhangchong
 * @create 2018/5/18
 * @Description
 * @CodeReviewer
 * @since
 */
public class RegistrationRequest {
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
    private List<UserAttrs> attribute;

    /**
     * CAName is the name of the CA to connect to
     */
    private String caName;

    public RegistrationRequest(RegistrationRequestNet registrationRequestNet) {
        caName = registrationRequestNet.getCaname();
        name = registrationRequestNet.getId();
        affiliation = registrationRequestNet.getAffiliationPath();
        attribute = registrationRequestNet.getAttrs();
        secret = registrationRequestNet.getSecret();
        type = registrationRequestNet.getType();
        maxEnrollments = registrationRequestNet.getMaxEnrollments();
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

    public List<UserAttrs> getAttribute() {
        return attribute;
    }

    public void setAttribute(List<UserAttrs> attribute) {
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
    public String toString() {
        return "RegistrationRequest{" +
                "maxEnrollments=" + maxEnrollments +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", secret='" + secret + '\'' +
                ", affiliation='" + affiliation + '\'' +
                ", attribute=" + attribute +
                ", caName='" + caName + '\'' +
                '}';
    }
}

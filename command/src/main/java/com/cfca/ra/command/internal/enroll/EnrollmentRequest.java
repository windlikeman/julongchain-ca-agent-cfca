package com.cfca.ra.command.internal.enroll;

import com.cfca.ra.command.config.CsrConfig;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用于调用服务器Restful接口用的参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentRequest {

    /**
     * The label used in HSM operations
     */
    private final String label;

    private final String username;
    private final String password;

    /**
     * Profile is the name of the signing profile to use in issuing the certificate
     */
    private final String profile;

    /**
     * CSR is Certificate Signing Request info
     */
    private final CsrConfig csrConfig;

    /**
     * CAName is the name of the CA to connect to
     */
    private final String caName;

    private EnrollmentRequest(Builder builder) {
        this.label = builder.label;
        this.username = builder.username;
        this.password = builder.password;
        this.profile = builder.profile;
        this.csrConfig = builder.csrConfig;
        this.caName = builder.caName;
    }

    public String getLabel() {
        return label;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public CsrConfig getCsrConfig() {
        return csrConfig;
    }

    public String getCaName() {
        return caName;
    }

    @Override
    public String toString() {
        return "EnrollmentRequest{" +
                "label='" + label + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", profile='" + profile + '\'' +
                ", csrConfig=" + csrConfig +
                ", caName='" + caName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnrollmentRequest that = (EnrollmentRequest) o;
        return Objects.equals(label, that.label) &&
                Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(profile, that.profile) &&
                Objects.equals(csrConfig, that.csrConfig) &&
                Objects.equals(caName, that.caName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(label, username, password, profile, csrConfig, caName);
    }

    private final static EnrollmentRequest NULL = new Builder(null,null,null,null,null).label(null).build();

    public boolean isNull() {
        return this.equals(NULL);
    }

    public static class Builder {
        private final String password;
        private final String username;
        private final String profile;
        private final CsrConfig csrConfig;
        private final String caName;
        /**
         * Optional:The label used in HSM operations
         */
        private String label = "";


        public Builder(String username, String password, String profile, CsrConfig csrConfig, String caName) {
            this.username = username;
            this.password = password;
            this.profile = profile;
            this.csrConfig = csrConfig;
            this.caName = caName;
        }

        public Builder label(String v){
            this.label = v;
            return this;
        }

        public EnrollmentRequest build() {
            return new EnrollmentRequest(this);
        }
    }
}

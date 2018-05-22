package com.cfca.ra.command.internal;

import com.cfca.ra.command.config.CsrConfig;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用于调用服务器Restful接口用的参数
 * @CodeReviewer
 * @since v3.0.0
 */
class EnrollmentRequest {
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

    static class Builder {
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

        public EnrollmentRequest build() {
            return new EnrollmentRequest(this);
        }
    }
}

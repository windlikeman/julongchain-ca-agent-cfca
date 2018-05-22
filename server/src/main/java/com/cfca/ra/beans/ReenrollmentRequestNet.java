package com.cfca.ra.beans;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description 用于调用服务器Restful接口用的参数
 * @CodeReviewer
 * @since v3.0.0
 */
public class ReenrollmentRequestNet {
    /**
     * A encoded string containing the CSR (Certificate Signing Request) based on PKCS #10.
     */
    private final String request;
    /**
     * The username of the signing profile to use when issuing the certificate.
     */
    private final String profile;
    /**
     * The label used in HSM operations
     */
    private final String label;
    /**
     * Name of the CA to direct traffic to within server.
     */
    private final String caname;

    private final CsrConfig csrInfo;

    private ReenrollmentRequestNet(Builder builder) {
        this.request = builder.request;
        this.profile = builder.profile;
        this.label = builder.label;
        this.caname = builder.caname;
        this.csrInfo = builder.csrInfo;
    }

    public String getRequest() {
        return request;
    }

    public String getProfile() {
        return profile;
    }

    public String getLabel() {
        return label;
    }

    public String getCaname() {
        return caname;
    }

    public CsrConfig getCsrInfo() {
        return csrInfo;
    }

    @Override
    public String toString() {
        return "EnrollmentRequestNet{" +
                "request='" + request + '\'' +
                ", profile='" + profile + '\'' +
                ", label='" + label + '\'' +
                ", caname='" + caname + '\'' +
                ", csrInfo=" + csrInfo +
                '}';
    }

    static class Builder {
        // Required
        /**
         * A encoded string containing the CSR (Certificate Signing Request) based on PKCS #10.
         */
        private final String request;
        /**
         * The username of the signing profile to use when issuing the certificate.
         */
        private final String profile;
        /**
         * Name of the CA to direct traffic to within server.
         */
        private final String caname;
        private final CsrConfig csrInfo;

        /**
         * Optional:The label used in HSM operations
         */
        private String label = "";

        Builder(String request, String profile, String caname, CsrConfig csrInfo) {
            this.request = request;
            this.profile = profile;
            this.caname = caname;
            this.csrInfo = csrInfo;
        }

        Builder label(String label) {
            this.label = label;
            return this;
        }

        ReenrollmentRequestNet build() {
            return new ReenrollmentRequestNet(this);
        }
    }
}

package com.cfca.ra.command.internal;

import com.cfca.ra.command.config.CsrConfig;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description enroll接口网络请求参数,用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentRequestNet {
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

    private EnrollmentRequestNet(Builder builder) {
        this.request = builder.request;
        this.profile = builder.profile;
        this.label = builder.label;
        this.caname = builder.caname;
        this.csrInfo = builder.csrInfo;
    }

    String getRequest() {
        return request;
    }

    String getProfile() {
        return profile;
    }

    String getLabel() {
        return label;
    }

    String getCaname() {
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

        EnrollmentRequestNet build() {
            return new EnrollmentRequestNet(this);
        }
    }
}

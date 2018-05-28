package com.cfca.ra.enroll;

import com.cfca.ra.beans.CsrConfig;

import java.util.Objects;

/**
 * @author zhangchong
 * @create 2018/5/11
 * @Description enroll接口网络请求参数, 用于调用服务器Restful接口
 * @CodeReviewer
 * @since v3.0.0
 */
public class EnrollmentRequestNet {
    /**
     * A encoded string containing the CSR (Certificate Signing Request) based on PKCS #10.
     */
    private final String request;
    /**
     * The name of the signing profile to use when issuing the certificate.
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

    public EnrollmentRequestNet(final String request, final String profile, final String label, final String caname, final CsrConfig csrInfo) {
        this.request = request;
        this.profile = profile;
        this.label = label;
        this.caname = caname;
        this.csrInfo = csrInfo;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentRequestNet that = (EnrollmentRequestNet) o;
        return Objects.equals(request, that.request) &&
                Objects.equals(profile, that.profile) &&
                Objects.equals(label, that.label) &&
                Objects.equals(caname, that.caname) &&
                Objects.equals(csrInfo, that.csrInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, profile, label, caname, csrInfo);
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
}
